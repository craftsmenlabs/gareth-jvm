package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.definitions.CreateDefinitionsRegistry
import org.craftsmenlabs.gareth.execution.definitions.DefinitionsRegistry
import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.validator.ExperimentDefinition
import org.craftsmenlabs.gareth.validator.GarethIllegalDefinitionException
import org.craftsmenlabs.gareth.validator.beans.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.RunContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
open class DefinitionFactory @Autowired constructor(private val applicationContext: ApplicationContext,
                                                    private val manager: DefinitionRegistryManager,
                                                    private val expressionParser: DurationExpressionParser) : ApplicationListener<ApplicationReadyEvent> {
    private val log: Logger = LoggerFactory.getLogger(DefinitionFactory::class.java)
    private lateinit var registry: DefinitionsRegistry

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        registry = CreateDefinitionsRegistry(event.applicationContext.getBeansOfType(ExperimentDefinition::class.java, true, false).values)
        log.info("Successfully created definition registry.")
        manager.sendRegistryToGarethHub(registry.toRegistryDTO())
        log.info("Sent registry to Gareth hub")
    }

    fun invokeGlueline(glueLine: String, type: GlueLineType, context: RunContext? = null): Any? {
        val method = registry.getMethod(glueLine, type) ?: throw GarethIllegalDefinitionException("No definition found for glue line '$glueLine'")
        return invokeMethod(method, glueLine, context)
    }

    fun getTimeToExecuteAssumption(glueLine: String): LocalDateTime {
        val method = registry.getMethod(glueLine, GlueLineType.TIME)
        var duration = if (method == null) expressionParser.parse(glueLine) else invokeMethod(method, glueLine) as Duration
        return expressionParser.calculateTimeDifferenceFromNow(duration
                ?: throw IllegalStateException("Not a valid expression"))
    }

    private fun invokeMethod(invokableMethod: InvokableMethod, glueLine: String, context: RunContext? = null): Any? =
            invokableMethod.invokeWith(glueLine, applicationContext.getBean(invokableMethod.method.declaringClass), context)

}