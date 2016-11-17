package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.execution.RunContext
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Pattern

class InvokableMethod {
    private val parameters = ArrayList<Class<*>>()
    val pattern: Pattern
    val method: Method
    val runcontextParameter: Boolean

    constructor(glueLine: String,
                method: Method,
                runcontextParameter: Boolean) {
        try {
            this.runcontextParameter = runcontextParameter
            pattern = Pattern.compile(glueLine)
            this.method = method
            parseMethod()
        } catch (e: Exception) {
            throw GarethDefinitionParseException(e)
        }
    }

    fun hasRunContext(): Boolean {
        return this.runcontextParameter
    }

    fun getMethodName(): String {
        return method.name
    }

    fun getClassName(): String {
        return method.declaringClass.name
    }

    fun invokeWith(glueLineInExperiment: String, declaringClassInstance: Any, runContext: RunContext) {
        val arguments = ArrayList<Any>()
        if (hasRunContext())
            arguments.add(runContext!!)
        val argumentValuesFromInputString = getArgumentValuesFromInputString(glueLineInExperiment)
        arguments.addAll(argumentValuesFromInputString)
        try {
            method.invoke(declaringClassInstance, *arguments.toArray())
        } catch (e: Exception) {
            throw GarethInvocationException(e)
        }

    }

    fun getRegexPatternForGlueLine(): String {
        return pattern.pattern()
    }

    fun getRegularParameters() =
            parameters.filter({ p -> p !== RunContext::class.java })


    fun getPattern(): String {
        return pattern.pattern()
    }

    private fun getArgumentValuesFromInputString(input: String): List<Any> {
        val parametersFromPattern = getParametersFromPattern(input.trim { it <= ' ' })
        val parameters = ArrayList<Any>()
        for (i in parametersFromPattern.indices) {
            val cls = getRegularParameters()[i]
            parameters.add(getValueFromString(cls, parametersFromPattern[i]))
        }
        return parameters
    }

    private fun parseMethod() {
        for (parameter in method.parameters) {
            val cls = parameter.type
            if (parameter.parameterizedType !== RunContext::class.java) {
                if (!isValidType(cls)) {
                    throw IllegalStateException("Parameter type $cls is not supported")
                }
                parameters.add(cls)
            }
        }
    }

    private fun isValidType(type: Type): Boolean {
        return type.typeName == "java.lang.String"
                || type.typeName == "int"
                || type.typeName == "long"
                || type.typeName == "double"
    }


    private fun getValueFromString(cls: Class<*>, stringVal: String): Any {
        if (cls == String::class.java) {
            return stringVal
        } else if (cls == java.lang.Integer.TYPE) {
            return java.lang.Integer.valueOf(stringVal).toInt()
        } else if (cls == java.lang.Long.TYPE) {
            return java.lang.Long.valueOf(stringVal).toLong()
        } else if (cls == java.lang.Double.TYPE) {
            return java.lang.Double.valueOf(stringVal).toDouble()
        }
        throw IllegalArgumentException("Parameter must be of class String, Int, Long or Double")
    }

    private fun getParametersFromPattern(s: String): List<String> {
        val output = ArrayList<String>()
        val matcher = pattern!!.matcher(s)
        if (!matcher.matches()) {
            throw IllegalArgumentException("Input string " + s + " could not be matched against pattern " + getPattern())
        }
        val groupCount = matcher.groupCount()
        val expectedParameters = getRegularParameters().size
        if (groupCount != expectedParameters) {
            throw IllegalArgumentException("Input string $s must have $expectedParameters parameters.")
        }
        for (i in 1..groupCount) {
            output.add(matcher.group(i))
        }
        return output
    }
}