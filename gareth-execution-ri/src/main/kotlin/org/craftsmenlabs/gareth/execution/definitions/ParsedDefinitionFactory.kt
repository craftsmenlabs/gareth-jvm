package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.validator.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.time.Duration

class ParsedDefinitionFactory {

    private val log = LoggerFactory.getLogger(ParsedDefinitionFactory::class.java)

    fun parse(clazz: Class<*>): ParsedDefinition {
        val parsedDefinition = ParsedDefinition()
        clazz.methods.forEach { m -> parseMethod(m, parsedDefinition) }
        return parsedDefinition
    }

    /**
     * Parse a single method

     * @param method
     * *
     * @param definition
     */
    private fun parseMethod(method: Method, definition: ParsedDefinition) {
        val baseline = getAnnotation(method, Baseline::class.java)
        if (baseline != null) {
            definition.baselineDefinitions.put(baseline.glueLine, createMethod(method, baseline.glueLine, baseline.description, baseline.humanReadable))
        }
        val assume = getAnnotation(method, Assume::class.java)
        if (assume != null) {
            definition.assumeDefinitions.put(assume.glueLine, createMethod(method, assume.glueLine, assume.description, assume.humanReadable, true))
        }
        val sucess = getAnnotation(method, Success::class.java)
        if (sucess != null) {
            definition.successDefinitions.put(sucess.glueLine, createMethod(method, sucess.glueLine, sucess.description, sucess.humanReadable))
        }
        val failure = getAnnotation(method, Failure::class.java)
        if (failure != null) {
            definition.failureDefinitions.put(failure.glueLine, createMethod(method, failure.glueLine, failure.description, failure.humanReadable))
        }
        val time = getAnnotation(method, Time::class.java)
        if (time != null) {
            definition.timeDefinitions.put(time.glueLine, createMethod(method,time.glueLine,time.description,time.humanReadable))
        }
    }

    fun <T> getAnnotation(method: Method, annotationClass: Class<T>): T? {
        return method.declaredAnnotations.filter { it.annotationClass.simpleName.equals(annotationClass.simpleName) }.firstOrNull() as T
    }

    private fun createMethod(method: Method,
                             glueLine: String,
                             description: String? = null,
                             humanReadable: String? = null,
                             expectBoolean: Boolean = false): InvokableMethod {
        val isBoolean = method.returnType == Boolean::class.java
        val isVoid = method.returnType != Void::class.java || method.returnType != Void.TYPE
        if (expectBoolean && !isBoolean)
            throw GarethIllegalDefinitionException("Method return type must be boolean but is ${method.returnType}")
        if (!expectBoolean && !isVoid)
            throw GarethIllegalDefinitionException("Method return type must be void but is ${method.returnType}")
        fun nonEmpty(str: String?): String? = if (str == null || str.isBlank()) null else str!!
        return InvokableMethod(
                glueLine = glueLine,
                description = description,
                method = method,
                humanReadable = nonEmpty(humanReadable) ?: nonEmpty(description) ?: glueLine,
                runcontextParameter = hasRunContextParameter(method))
    }

    /**
     * Register duration based on method outcome

     * @param method
     * *
     * @param glueLine
     * *
     * @param durationMap
     */


    private fun hasRunContextParameter(method: Method): Boolean {
        return method.parameterCount > 0 && InvokableMethod.isContextParameter(method.parameterTypes[0])// == "org.craftsmenlabs.gareth.validator.model.RunContext"
    }

    private fun isTimeMethod(method: Method): Boolean {
        return method.returnType.isAssignableFrom(Duration::class.java)
    }

}