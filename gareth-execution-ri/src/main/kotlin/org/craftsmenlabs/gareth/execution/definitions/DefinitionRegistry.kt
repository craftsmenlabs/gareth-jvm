package org.craftsmenlabs.gareth.execution.definitions

import com.google.common.collect.Lists
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException
import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

class DefinitionRegistry(val definitionFactory: DefinitionFactory) {
    private val regexes = HashMap<String, Pattern>()
    private val durationExpressionParser = DurationExpressionParser()
    private val baselineDefinitions = HashMap<String, InvokableMethod>()
    private val assumeDefinitions = HashMap<String, InvokableMethod>()
    private val successDefinitions = HashMap<String, InvokableMethod>()
    private val failureDefinitions = HashMap<String, InvokableMethod>()
    private val timeDefinitions = HashMap<String, Duration>()

    fun getAllMethodDescriptorForExecutionType(type: ExecutionType): List<InvokableMethod> {
        return when (type) {
            ExecutionType.BASELINE -> Lists.newArrayList(baselineDefinitions.values)
            ExecutionType.ASSUME -> Lists.newArrayList(assumeDefinitions.values)
            ExecutionType.SUCCESS -> Lists.newArrayList(successDefinitions.values)
            ExecutionType.FAILURE -> Lists.newArrayList(failureDefinitions.values)
        }
    }

    fun getAllTimeDefinitions(): List<Duration> {
        return Lists.newArrayList(timeDefinitions.values)
    }

    fun getMethodDescriptorForExecutionType(glueLine: String, type: ExecutionType): InvokableMethod {
        return when (type) {
            ExecutionType.BASELINE -> getDefinition(baselineDefinitions, glueLine)
            ExecutionType.ASSUME -> getDefinition(assumeDefinitions, glueLine)
            ExecutionType.SUCCESS -> getDefinition(successDefinitions, glueLine)
            ExecutionType.FAILURE -> getDefinition(failureDefinitions, glueLine)
        }
    }

    fun addParsedDefinition(parsedDefinition: ParsedDefinition) {
        parsedDefinition.baselineDefinitions.forEach { entry -> addDefinition(baselineDefinitions, entry.key, entry.value) }
        parsedDefinition.assumeDefinitions.forEach { entry -> addDefinition(assumeDefinitions, entry.key, entry.value) }
        parsedDefinition.failureDefinitions.forEach { entry -> addDefinition(failureDefinitions, entry.key, entry.value) }
        parsedDefinition.successDefinitions.forEach { entry -> addDefinition(successDefinitions, entry.key, entry.value) }
        parsedDefinition.timeDefinitions.forEach { entry -> addDefinition(timeDefinitions, entry.key, entry.value) }
    }

    fun getDurationForGlueline(glueLine: String): Duration = getTimeDefinition(timeDefinitions, glueLine)

    fun invokeMethodByType(glueLine: String, type: ExecutionType, request: ExecutionRequestDTO): RunContext {
        val method = getMethodDescriptorForExecutionType(glueLine, type)
        val context = RunContext.create(request)
        try {
            val declaringClass = getMethodDescriptorForExecutionType(glueLine, type).method.declaringClass
            val declaringClassInstance = definitionFactory.getInstanceForClass(declaringClass)
            method.invokeWith(glueLine, declaringClassInstance, context)
            return context
        } catch (e: ReflectiveOperationException) {
            throw GarethInvocationException(e)
        }
    }

    private fun getDefinition(valueMap: Map<String, InvokableMethod>, experimentLine: String): InvokableMethod {
        val match = valueMap.values.filter({ md -> matchesPattern(experimentLine, md.getRegexPatternForGlueLine()) }).firstOrNull()
        return match ?: throw GarethUnknownDefinitionException(String.format("No definition found for glue line '%s'", experimentLine))
    }

    private fun <T> getTimeDefinition(valueMap: Map<String, T>, experimentLine: String): T {
        val match = valueMap.keys.filter({ annotationPattern -> matchesPattern(experimentLine, annotationPattern) }).firstOrNull()
        if (match != null) {
            return valueMap[match]!!
        } else {
            //if no custom Time method is available, try to parse the experiment glueline to a common expression
            val duration = durationExpressionParser.parse(experimentLine)
            return duration.orElseThrow { GarethUnknownDefinitionException("No definition found for glue line " + experimentLine) } as T
        }
    }

    private fun <T> addDefinition(valueMap: MutableMap<String, T>, glueLine: String, definition: T) {
        if (!valueMap.containsKey(glueLine)) {
            valueMap.put(glueLine, definition)
        } else {
            throw GarethAlreadyKnownDefinitionException(String.format("Glue line already registered for '%s'", glueLine))
        }
    }

    private fun matchesPattern(experimentLine: String, pattern: String): Boolean {
        if (experimentLine == null || pattern == null) {
            return false
        }
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