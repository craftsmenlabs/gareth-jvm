package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.api.annotation.*
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.execution.services.DefinitionFactory
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.time.Duration

class ParsedDefinitionFactory(val definitionFactory: DefinitionFactory) {

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
            definition.baselineDefinitions.put(baseline.glueLine, createMethod(method, baseline.glueLine, baseline.description))
        }
        val assume = getAnnotation(method, Assume::class.java)
        if (assume != null) {
            definition.assumeDefinitions.put(assume.glueLine, createMethod(method, assume.glueLine, assume.description, true))
        }
        val sucess = getAnnotation(method, Success::class.java)
        if (sucess != null) {
            definition.successDefinitions.put(sucess.glueLine, createMethod(method, sucess.glueLine, sucess.description))
        }
        val failure = getAnnotation(method, Failure::class.java)
        if (failure != null) {
            definition.failureDefinitions.put(failure.glueLine, createMethod(method, failure.glueLine, failure.description))
        }
        val time = getAnnotation(method, Time::class.java)
        if (time != null) {
            registerDuration(method, time.glueLine, time.description, definition.timeDefinitions)
        }
    }

    fun <T> getAnnotation(method: Method, annotationClass: Class<T>): T? {
        return method.declaredAnnotations.filter { it.annotationClass.simpleName.equals(annotationClass.simpleName) }.firstOrNull() as T
    }

    private fun createMethod(method: Method, glueLine: String, description: String, expectBoolean: Boolean = false): InvokableMethod {
        val isBoolean = method.returnType == Boolean::class.java
        val isVoid = method.returnType != Void::class.java || method.returnType != Void.TYPE
        if (expectBoolean && !isBoolean)
            throw GarethDefinitionParseException("Method return type must be boolean but is ${method.returnType}")
        if (!expectBoolean && !isVoid)
            throw GarethDefinitionParseException("Method return type must be void but is ${method.returnType}")
        return InvokableMethod(glueLine = glueLine, description = description, method = method, runcontextParameter = hasRunContextParameter(method))
    }

    /**
     * Register duration based on method outcome

     * @param method
     * *
     * @param glueLine
     * *
     * @param durationMap
     */
    private fun registerDuration(method: Method, glueLine: String, description: String, durationMap: MutableMap<String, Duration>) {
        if (isTimeMethod(method)) {
            val tmpDefinition: Any?
            try {
                tmpDefinition = definitionFactory.getInstanceForClass(method.declaringClass)
            } catch(e: Exception) {
                throw GarethDefinitionParseException("Could not instantiate instance for class ${method.declaringClass}")
            }
            try {
                durationMap.put(glueLine, method.invoke(tmpDefinition) as Duration)
            } catch (e: Exception) {
                throw GarethInvocationException(e)
            }
        } else {
            throw IllegalStateException(String.format("Method %s with glue line '%s' is not a valid method (no duration return type)", method.name, glueLine))
        }
    }


    private fun hasRunContextParameter(method: Method): Boolean {
        val hasParam = method.parameterCount > 0 && InvokableMethod.isContextParameter(method.parameterTypes[0])// == "org.craftsmenlabs.gareth.execution.RunContext"
        println(method.parameterTypes[0].name)
        return hasParam
    }

    private fun isTimeMethod(method: Method): Boolean {
        return method.returnType.isAssignableFrom(Duration::class.java)
    }

}