package org.craftsmenlabs.gareth.execution.services

import com.google.common.reflect.ClassPath
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException
import org.craftsmenlabs.gareth.execution.invoker.DefaultStorage
import org.craftsmenlabs.gareth.execution.invoker.DefinitionFactory
import org.craftsmenlabs.gareth.execution.invoker.MethodInvoker
import org.craftsmenlabs.gareth.execution.invoker.RegexMethodDescriptor
import org.craftsmenlabs.gareth.execution.parser.ParsedDefinitionFactory
import org.craftsmenlabs.gareth.execution.registry.DefinitionRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
open class GluelineCodeRegistry @Autowired constructor(definitionFactory: DefinitionFactory) {

    val log: Logger = LoggerFactory.getLogger(GluelineCodeRegistry::class.java)
    val factory = ParsedDefinitionFactory(definitionFactory)
    val methodInvoker = MethodInvoker(definitionFactory)
    val definitionRegistry = DefinitionRegistry()
    val storage = DefaultStorage()

    @PostConstruct
    fun init() {
        //val classes = listOf<Class<*>>(GetSaleAmounts::class.java, ResultSteps::class.java, SaleOfFruit::class.java, SaleOfWidgets::class.java)
        val classes = getClassesInPackage("org.craftsmenlabs.gareth.execution.spi")
        classes.forEach { clz ->
            log.info("Parsing class ${clz.name}")
            definitionRegistry.addParsedDefinition(factory.parse(clz))
            log.info("Successfully parsed ${clz.simpleName}")
        }
    }

    fun getClassesInPackage(packageName: String): List<Class<*>> {
        val classesInfo = ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(packageName)
        return classesInfo.map { Class.forName(it.name) }
    }


    fun getBaselineMethod(glueline: String): RegexMethodDescriptor = definitionRegistry.getMethodDescriptorForBaseline(glueline)

    fun getAssumptionMethod(glueline: String): RegexMethodDescriptor = definitionRegistry.getMethodDescriptorForAssume(glueline)

    fun getFailureMethod(glueline: String): RegexMethodDescriptor = definitionRegistry.getMethodDescriptorForFailure(glueline)

    fun getSuccessMethod(glueline: String): RegexMethodDescriptor = definitionRegistry.getMethodDescriptorForSuccess(glueline)

    fun executeBaseline(glueline: String) {
        methodInvoker.invoke(glueline, getBaselineMethod(glueline), storage)
    }

    fun executeAssumption(glueline: String) {
        methodInvoker.invoke(glueline, getAssumptionMethod(glueline), storage)
    }

    fun executeFailure(glueline: String) {
        methodInvoker.invoke(glueline, getFailureMethod(glueline), storage)
    }

    fun executeSuccess(glueline: String) {
        methodInvoker.invoke(glueline, getSuccessMethod(glueline), storage)
    }

    fun getDurationInMillis(glueline: String): Long {
        try {
            return definitionRegistry.getDurationForTime(glueline).toMillis()
        } catch (e: GarethUnknownDefinitionException) {
            throw GarethInvocationException(e)
        }
    }


}



