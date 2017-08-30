package org.craftsmenlabs.gareth.execution.definitions

import org.craftsmenlabs.gareth.validator.ExperimentDefinition
import org.craftsmenlabs.gareth.validator.GarethIllegalDefinitionException
import org.craftsmenlabs.gareth.validator.model.GlueLineType


fun CreateDefinitionsRegistry(beans: Collection<ExperimentDefinition>): DefinitionsRegistry {
    val classes = beans.map { it.javaClass }
    val registry: Map<GlueLineType, MutableMap<String, InvokableMethod>> = mapOf(
            Pair(GlueLineType.BASELINE, mutableMapOf()),
            Pair(GlueLineType.ASSUME, mutableMapOf()),
            Pair(GlueLineType.TIME, mutableMapOf()),
            Pair(GlueLineType.SUCCESS, mutableMapOf()),
            Pair(GlueLineType.FAILURE, mutableMapOf())
    )

    fun addParsedDefinition(clz: Class<*>) {
        val factory = ParsedDefinitionFactory()
        fun addDefinition(type: GlueLineType, glueLine: String, method: InvokableMethod) {
            val valueMap = registry[type]!!
            if (!valueMap.containsKey(glueLine)) {
                valueMap.put(glueLine, method)
            } else {
                throw GarethIllegalDefinitionException("Glue line already registered for $glueLine")
            }
        }

        val parsedDefinition = factory.parse(clz)
        parsedDefinition.baselineDefinitions.forEach { addDefinition(GlueLineType.BASELINE, it.key, it.value) }
        parsedDefinition.assumeDefinitions.forEach { addDefinition(GlueLineType.ASSUME, it.key, it.value) }
        parsedDefinition.failureDefinitions.forEach { addDefinition(GlueLineType.FAILURE, it.key, it.value) }
        parsedDefinition.successDefinitions.forEach { addDefinition(GlueLineType.SUCCESS, it.key, it.value) }
        parsedDefinition.timeDefinitions.forEach { addDefinition(GlueLineType.TIME, it.key, it.value) }
    }
    classes.forEach { addParsedDefinition(it) }
    return DefinitionsRegistry(registry)
}

