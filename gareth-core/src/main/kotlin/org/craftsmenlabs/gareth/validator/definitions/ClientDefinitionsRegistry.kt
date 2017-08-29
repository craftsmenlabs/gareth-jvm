package org.craftsmenlabs.gareth.validator.definitions

import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Glueline

class ClientDefinitionsRegistry(private val glueLinesPerCategory: Map<GlueLineType, Set<Glueline>>) {

    fun getGluelinesPerCategory(glueLineType: GlueLineType): Set<Glueline> {
        return glueLinesPerCategory[glueLineType] ?: throw IllegalStateException("Not properly initialized")
    }
}
