package org.craftsmenlabs.gareth.execution.services

import com.google.common.reflect.ClassPath
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException
import org.craftsmenlabs.gareth.api.execution.ExecutionRequest
import org.craftsmenlabs.gareth.api.execution.ExecutionRunContext
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.execution.definitions.ParsedDefinitionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import javax.annotation.PostConstruct

@Service
open class DefinitionRegistry @Autowired constructor(val definitionFactory: DefinitionFactory) {
    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)

    @Value("\${definitions.package:org.craftsmenlabs.gareth.execution.spi}")
    lateinit var definitionsPackage: String

    val factory = ParsedDefinitionFactory(definitionFactory)
    private val regexes = HashMap<String, Pattern>()
    private val baselineDefinitions = HashMap<String, InvokableMethod>()
    private val assumeDefinitions = HashMap<String, InvokableMethod>()
    private val successDefinitions = HashMap<String, InvokableMethod>()
    private val failureDefinitions = HashMap<String, InvokableMethod>()
    private val timeDefinitions = HashMap<String, Duration>()


    @PostConstruct
    fun init() {
        val classes = getClassesInPackage(definitionsPackage)
        classes.forEach { clz ->
            log.info("Parsing class ${clz.name}")
            addParsedDefinition(clz)
            log.info("Successfully parsed ${clz.simpleName}")
        }
    }

    private fun getClassesInPackage(packageName: String): List<Class<*>> {
        val classesInfo = ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(packageName)
        return classesInfo.map { Class.forName(it.name) }
    }

    fun getGlueLinesPerCategory(): Map<GlueLineType, Set<String>> {
        val allPatterns = HashMap<GlueLineType, Set<String>>()
        allPatterns.put(GlueLineType.ASSUME, assumeDefinitions.keys)
        allPatterns.put(GlueLineType.BASELINE, baselineDefinitions.keys)
        allPatterns.put(GlueLineType.SUCCESS, successDefinitions.keys)
        allPatterns.put(GlueLineType.FAILURE, failureDefinitions.keys)
        allPatterns.put(GlueLineType.TIME, timeDefinitions.keys)
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
                throw GarethAlreadyKnownDefinitionException("Glue line already registered for $glueLine")
            }
        }

        val parsedDefinition = factory.parse(clz)
        parsedDefinition.baselineDefinitions.forEach { addDefinition(baselineDefinitions, it.key, it.value) }
        parsedDefinition.assumeDefinitions.forEach { addDefinition(assumeDefinitions, it.key, it.value) }
        parsedDefinition.failureDefinitions.forEach { addDefinition(failureDefinitions, it.key, it.value) }
        parsedDefinition.successDefinitions.forEach { addDefinition(successDefinitions, it.key, it.value) }
        parsedDefinition.timeDefinitions.forEach { addDefinition(timeDefinitions, it.key, it.value) }
    }

    fun getTimeForGlueline(glueLine: String): Duration {
        val match = timeDefinitions.keys.filter({ annotationPattern -> matchesPattern(glueLine, annotationPattern) }).firstOrNull()
        return timeDefinitions[match] ?: throw GarethUnknownDefinitionException("No time definition found for glue line $glueLine")
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
        return match ?: throw GarethUnknownDefinitionException("No definition found for glue line '$experimentLine'")
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