package org.craftsmenlabs.gareth.execution.services.definitions

import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.execution.definitions.ParsedDefinitionFactory
import org.craftsmenlabs.gareth.execution.services.ExecutionService
import org.craftsmenlabs.gareth.validator.ExperimentDefinition
import org.craftsmenlabs.gareth.validator.GarethIllegalDefinitionException
import org.craftsmenlabs.gareth.validator.beans.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.DefinitionInfo
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Glueline
import org.craftsmenlabs.gareth.validator.model.RunContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern

@Service
open class DefinitionRegistryService @Autowired constructor(private val definitionFactory: DefinitionFactory,
                                                            private val expressionParser: DurationExpressionParser) : ApplicationListener<ApplicationReadyEvent> {

    val log: Logger = LoggerFactory.getLogger(ExecutionService::class.java)

    val factory = ParsedDefinitionFactory(definitionFactory)
    private val regexes = HashMap<String, Pattern>()
    private val baselineDefinitions = HashMap<String, InvokableMethod>()
    private val assumeDefinitions = HashMap<String, InvokableMethod>()
    private val successDefinitions = HashMap<String, InvokableMethod>()
    private val failureDefinitions = HashMap<String, InvokableMethod>()
    private val timeDefinitions = HashMap<String, InvokableMethod>()

    var timeGlueLines: MutableSet<Glueline> = mutableSetOf()
    private lateinit var glueLinesPerCategory: Map<GlueLineType, Set<Glueline>>

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val beansOfType = event.applicationContext.getBeansOfType(ExperimentDefinition::class.java, true, false)
        val classes = beansOfType.values.map { it.javaClass }
        log.info("Found ${classes.size} implementing beans of ExperimentDefinition")
        beansOfType.keys.forEach { log.info("$it") }
        classes.forEach { clz ->
            log.info("Parsing class ${clz.name}")
            addParsedDefinition(clz)
            log.info("Successfully parsed ${clz.simpleName}")
        }
        glueLinesPerCategory = createGluelinesPerCategoryMap()
        timeGlueLines.addAll(createDefaultTimeGlueLinePatterns())
        timeGlueLines.addAll(glueLinesPerCategory[GlueLineType.TIME].orEmpty())
    }

    private fun addParsedDefinition(clz: Class<*>) {
        fun <T> addDefinition(valueMap: MutableMap<String, T>, glueLine: String, definition: T) {
            if (!valueMap.containsKey(glueLine)) {
                valueMap.put(glueLine, definition)
            } else {
                throw GarethIllegalDefinitionException("Glue line already registered for $glueLine")
            }
        }

        val parsedDefinition = factory.parse(clz)
        parsedDefinition.baselineDefinitions.forEach { addDefinition(baselineDefinitions, it.key, it.value) }
        parsedDefinition.assumeDefinitions.forEach { addDefinition(assumeDefinitions, it.key, it.value) }
        parsedDefinition.failureDefinitions.forEach { addDefinition(failureDefinitions, it.key, it.value) }
        parsedDefinition.successDefinitions.forEach { addDefinition(successDefinitions, it.key, it.value) }
        parsedDefinition.timeDefinitions.forEach { addDefinition(timeDefinitions, it.key, it.value) }
    }

    private fun createGluelinesPerCategoryMap(): Map<GlueLineType, Set<Glueline>> {
        fun toGlueline(map: Map<String, InvokableMethod>): Set<Glueline> = map.map { Glueline(it.key, it.value.humanReadable) }.toSet()

        val allPatterns = HashMap<GlueLineType, Set<Glueline>>()
        allPatterns.put(GlueLineType.ASSUME, toGlueline(assumeDefinitions))
        allPatterns.put(GlueLineType.BASELINE, toGlueline(baselineDefinitions))
        allPatterns.put(GlueLineType.SUCCESS, toGlueline(successDefinitions))
        allPatterns.put(GlueLineType.FAILURE, toGlueline(failureDefinitions))
        allPatterns.put(GlueLineType.TIME, toGlueline(timeDefinitions))
        return allPatterns
    }

    private fun getMethodDescriptorForExecutionType(glueLine: String, type: GlueLineType): InvokableMethod {
        fun getDefinition(valueMap: Map<String, InvokableMethod>, experimentLine: String): InvokableMethod {
            val match = valueMap.values.filter({ md -> matchesPattern(experimentLine, md.getRegexPatternForGlueLine()) }).firstOrNull()
            return match ?: throw GarethIllegalDefinitionException("No definition found for glue line '$experimentLine'")
        }
        return when (type) {
            GlueLineType.BASELINE -> getDefinition(baselineDefinitions, glueLine)
            GlueLineType.ASSUME -> getDefinition(assumeDefinitions, glueLine)
            GlueLineType.TIME -> getDefinition(timeDefinitions, glueLine)
            GlueLineType.SUCCESS -> getDefinition(successDefinitions, glueLine)
            GlueLineType.FAILURE -> getDefinition(failureDefinitions, glueLine)
        }
    }

    fun getDefinitionInfoForGluelineAndType(glueLine: String, type: GlueLineType): DefinitionInfo {
        val method = getMethodDescriptorForExecutionType(glueLine, type)
        return DefinitionInfo(
                glueline = method.getRegexPatternForGlueLine(),
                method = method.getMethodName(),
                className = method.getClassName(),
                humanReadable = method.humanReadable,
                description = method.description)
    }

    fun invokeGlueline(glueLine: String, type: GlueLineType, context: RunContext? = null): Any? =
            invokeMethod(getMethodDescriptorForExecutionType(glueLine, type), glueLine, context)


    private fun invokeMethod(invokableMethod: InvokableMethod, glueLine: String, context: RunContext? = null): Any? =
            invokableMethod.invokeWith(glueLine, getInstanceForClass(invokableMethod.method.declaringClass), context)


    fun getTimeToExecuteAssumption(glueLine: String): LocalDateTime {
        val match = timeDefinitions.keys.filter({ annotationPattern -> matchesPattern(glueLine, annotationPattern) }).firstOrNull()
        var duration = if (match == null) expressionParser.parse(glueLine) else invokeGlueline(glueLine, GlueLineType.TIME) as Duration
        return expressionParser.calculateTimeDifferenceFromNow(duration
                ?: throw IllegalStateException("Not a valid expression"))
    }


    private fun matchesPattern(experimentLine: String, pattern: String): Boolean {
        if (pattern.isBlank())
            return false
        return getGlueLinePattern(pattern).matcher(experimentLine).matches()
    }

    private fun getGlueLinePattern(pattern: String): Pattern {
        if (regexes.containsKey(pattern)) {
            return regexes[pattern]!!
        }
        val compiled = Pattern.compile(pattern)
        regexes.put(pattern, compiled)
        return compiled
    }

    private fun createDefaultTimeGlueLinePatterns(): List<Glueline> {
        return listOf(Glueline("^(\\d+) seconds?$", "<number> seconds"),
                Glueline("^(\\d+) minutes?$", "<number> minutes"),
                Glueline("^(\\d+) hours?$", "<number> hours"),
                Glueline("^(\\d+) days?$", "^<number> days"),
                Glueline("^(\\d+) weeks?$", "<number> weeks"),
                Glueline("^(\\d+) months?$", "<number> months"),
                Glueline("^(\\d+) years?$", "<number> years"))
    }

    fun getInstanceForClass(clazz: Class<*>) = definitionFactory.getInstanceForClass(clazz)

}