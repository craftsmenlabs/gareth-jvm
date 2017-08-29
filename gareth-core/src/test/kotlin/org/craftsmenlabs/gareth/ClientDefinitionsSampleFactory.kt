package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.validator.definitions.ClientDefinitionsRegistry
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Glueline


object ClientDefinitionsSampleFactory {
    fun create(): ClientDefinitionsRegistry {
        val baseline = Glueline("sale of .*?", "sale of <product>")
        val assume = Glueline("has risen by \\d+ percent", "has risen by <number> percent")
        val assume2 = Glueline("has risen by \\d+ items", "has risen by <number> items")
        val time = Glueline("next Easter", "next Easter")
        val success = Glueline("send .*? to developers", "send <incentive> to developers")
        val failure = Glueline("fire .*?", "fire someone")
        val gluelinesPerCategory = mapOf(
                Pair(GlueLineType.ASSUME, setOf(assume, assume2)),
                Pair(GlueLineType.BASELINE, setOf(baseline)),
                Pair(GlueLineType.TIME, setOf(time)),
                Pair(GlueLineType.SUCCESS, setOf(success)),
                Pair(GlueLineType.FAILURE, setOf(failure)))
        return ClientDefinitionsRegistry(gluelinesPerCategory)
    }
}