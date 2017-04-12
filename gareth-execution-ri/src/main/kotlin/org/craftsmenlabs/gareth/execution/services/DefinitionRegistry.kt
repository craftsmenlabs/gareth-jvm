package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.execution.definitions.ParsedDefinitionFactory
import org.craftsmenlabs.gareth.validator.ExperimentDefinition
import org.craftsmenlabs.gareth.validator.GarethIllegalDefinitionException
import org.craftsmenlabs.gareth.validator.GarethInvocationException
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.model.ExecutionRunContext
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import javax.annotation.PostConstruct

@Service
open class DefinitionRegistry @Autowired constructor(val definitionFactory: DefinitionFactory, val definitions: List<ExperimentDefinition>) {
    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)

    val factory = ParsedDefinitionFactory(definitionFactory)
    private val regexes = HashMap<String, Pattern>()
    private val baselineDefinitions = HashMap<String, InvokableMethod>()
    private val assumeDefinitions = HashMap<String, InvokableMethod>()
    private val successDefinitions = HashMap<String, InvokableMethod>()
    private val failureDefinitions = HashMap<String, InvokableMethod>()
    private val timeDefinitions = HashMap<String, Pair<String, Duration>>()

    @PostConstruct
    fun init() {
        val classes = definitions.map { it.javaClass }
        log.info("Found ${classes.size} classes.")
        classes.forEach { clz ->
            log.info("Parsing class ${clz.name}")
            addParsedDefinition(clz)
            log.info("Successfully parsed ${clz.simpleName}")
        }
    }

    fun getGlueLinesPerCategory(): Map<GlueLineType, Set<Pair<String, String?>>> {
        fun toPair(map: Map<String, InvokableMethod>): Set<Pair<String, String?>> = map.map { Pair(it.key, it.value.humanReadable) }.toSet()

        val allPatterns = HashMap<GlueLineType, Set<Pair<String, String?>>>()
        allPatterns.put(GlueLineType.ASSUME, toPair(assumeDefinitions))
        allPatterns.put(GlueLineType.BASELINE, toPair(baselineDefinitions))
        allPatterns.put(GlueLineType.SUCCESS, toPair(successDefinitions))
        allPatterns.put(GlueLineType.FAILURE, toPair(failureDefinitions))
        allPatterns.put(GlueLineType.TIME, timeDefinitions.map { Pair(it.key, it.value.first) }.toSet())
        return allPatterns
    }


    fun getMethodDescriptorForExecutionType(glueLine: String, type: ExecutionType): InvokableMethod {
        return when (type) {
            ExecutionType.BASELINE -> getDefinition(baselineDefinitions, glueLine)
            ExecutionType.ASSUME -> getDefinition(assumeDefinitions, glueLine)
            ExecutionType.SUCCESS -> getDefinition(successDefinitions, glueLine)
            ExecutionType.FAILURE -> getDefinition(failureDefinitions, glueLine)
        }
    }

    fun addParsedDefinition(clz: Class<*>) {

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

    fun getTimeForGlueline(glueLine: String): Pair<String, Duration> {
        val match = timeDefinitions.keys.filter({ annotationPattern -> matchesPattern(glueLine, annotationPattern) }).firstOrNull()
        return timeDefinitions[match] ?: throw GarethIllegalDefinitionException("No time definition found for glue line $glueLine")
    }


    fun invokeAssumptionMethod(glueLine: String, request: ExecutionRequest): Pair<Boolean, ExecutionRunContext> {
        val context = RunContext.create(request)
        val result = invokeMethodByType(glueLine, ExecutionType.ASSUME, context) as Boolean
        return Pair(result, context)
    }

    fun invokeVoidMethodByType(glueLine: String, type: ExecutionType, request: ExecutionRequest): ExecutionRunContext {
        val context = RunContext.create(request)
        invokeMethodByType(glueLine, type, context)
        return context
    }

    fun invokeMethodByType(glueLine: String, type: ExecutionType, context: ExecutionRunContext): Any? {
        val method = getMethodDescriptorForExecutionType(glueLine, type)
        try {
            val declaringClass = getMethodDescriptorForExecutionType(glueLine, type).method.declaringClass
            val declaringClassInstance = definitionFactory.getInstanceForClass(declaringClass)
            return method.invokeWith(glueLine, declaringClassInstance, context)
        } catch (e: ReflectiveOperationException) {
            throw GarethInvocationException(e)
        }
    }

    private fun getDefinition(valueMap: Map<String, InvokableMethod>, experimentLine: String): InvokableMethod {
        val match = valueMap.values.filter({ md -> matchesPattern(experimentLine, md.getRegexPatternForGlueLine()) }).firstOrNull()
        return match ?: throw GarethIllegalDefinitionException("No definition found for glue line '$experimentLine'")
    }

    private fun matchesPattern(experimentLine: String, pattern: String): Boolean {
        if (pattern.isBlank())
            return false;
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

}