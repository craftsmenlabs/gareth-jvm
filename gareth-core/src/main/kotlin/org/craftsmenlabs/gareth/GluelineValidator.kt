package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth.model.Gluelines

interface GluelineValidator {
    fun gluelinesAreValid(experiment: Gluelines): Boolean
    fun gluelineIsValid(type: GlueLineType, glueline: String): Boolean
}
