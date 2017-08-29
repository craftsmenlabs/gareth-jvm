package org.craftsmenlabs.gareth.execution.services.definitions

import org.springframework.beans.factory.BeanExpressionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.lang.reflect.Constructor

@Service
open class DefinitionFactory @Autowired constructor(val applicationContext: ApplicationContext) {

    fun getInstanceForClass(clazz: Class<*>): Any = getDefinitionFromContext(clazz) ?: createDefinitionByReflection(clazz)


    private fun createDefinitionByReflection(clazz: Class<*>): Any {
        var constructor: Constructor<*>? = null
        var declaringClassInstance: Any? = null

        val memberClass = clazz.isMemberClass
        val requiredConstructorArguments = if (memberClass) 1 else 0 //

        if (memberClass) {
            declaringClassInstance = getInstanceForClass(clazz.declaringClass)
        }
        for (declaredConstructor in clazz.declaredConstructors) {
            if (declaredConstructor.genericParameterTypes.size == requiredConstructorArguments) {
                constructor = declaredConstructor
                break
            }
        }
        // If a valid constructor is available
        if (null != constructor) {
            val instance: Any
            constructor.isAccessible = true
            if (memberClass) {
                instance = constructor.newInstance(declaringClassInstance)
            } else {
                instance = constructor.newInstance()
            }
            return instance
        }
        throw InstantiationException(String.format("Class %s has no zero argument argument constructor", clazz))
    }

    private fun getDefinitionFromContext(clazz: Class<*>): Any? {
        try {
            return applicationContext.getBean(clazz)
        } catch (e: BeanExpressionException) {
            return null
        }

    }

}