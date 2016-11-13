package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.api.annotation.*
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException
import org.craftsmenlabs.gareth.execution.RunContext
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.Duration


class ParsedDefinitionFactory(private val definitionFactory: DefinitionFactory) {

    private val LOGGER = LoggerFactory.getLogger(ParsedDefinitionFactory::class.java)

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
            definition.baselineDefinitions.put(baseline.glueLine, createMethod(method, baseline.glueLine))
        }
        val assume = getAnnotation(method, Assume::class.java)
        if (assume != null) {
            definition.assumeDefinitions.put(assume.glueLine, createMethod(method, assume.glueLine))
        }
        val sucess = getAnnotation(method, Success::class.java)
        if (sucess != null) {
            definition.successDefinitions.put(sucess.glueLine, createMethod(method, sucess.glueLine))
        }
        val failure = getAnnotation(method, Failure::class.java)
        if (failure != null) {
            definition.failureDefinitions.put(failure.glueLine, createMethod(method, failure.glueLine))
        }
        val time = getAnnotation(method, Time::class.java)
        if (time != null) {
            registerDuration(method, time.glueLine, definition.timeDefinitions)
        }
    }

    fun <T> getAnnotation(method: Method, annotationClass: Class<T>): T? {
        return method.declaredAnnotations.filter { an -> an.annotationClass.simpleName.equals(annotationClass.simpleName) }.firstOrNull() as T
    }

    private fun createMethod(method: Method, glueLine: String): InvokableMethod {
        if (isValidMethod(method)) {
            LOGGER.info("Found valid method {} for glueline {}", method.name, glueLine)
            return InvokableMethod(glueLine, method, hasRunContextParameter(method))

        } else {
            throw IllegalStateException(String.format("Method %s with glue line '%s' is not a valid method (no void return type)", method.name, glueLine))
        }
    }

    /**
     * Register duration based on method outcome

     * @param method
     * *
     * @param glueLine
     * *
     * @param durationMap
     */
    private fun registerDuration(method: Method, glueLine: String, durationMap: MutableMap<String, Duration>) {
        if (isTimeMethod(method)) {
            try {
                val tmpDefinition = definitionFactory.getInstanceForClass(method.declaringClass)
                durationMap.put(glueLine, method.invoke(tmpDefinition) as Duration)
            } catch (e: IllegalAccessException) {
                throw GarethDefinitionParseException(e)
            } catch (e: InstantiationException) {
                throw GarethDefinitionParseException(e)
            } catch (e: InvocationTargetException) {
                throw GarethDefinitionParseException(e)
            }

        } else {
            throw IllegalStateException(String.format("Method %s with glue line '%s' is not a valid method (no duration return type)", method.name, glueLine))
        }
    }

    private fun isValidMethod(method: Method): Boolean {
        return method.returnType == Void::class.java || method.returnType == Void.TYPE
    }

    private fun hasRunContextParameter(method: Method): Boolean {
        val hasParam = method.parameterCount > 0 && method.parameterTypes[0] == RunContext::class.java// == "org.craftsmenlabs.gareth.execution.RunContext"
        println(method.parameterTypes[0].name)
        return hasParam
    }

    private fun isTimeMethod(method: Method): Boolean {
        return method.returnType.isAssignableFrom(Duration::class.java)
    }

}