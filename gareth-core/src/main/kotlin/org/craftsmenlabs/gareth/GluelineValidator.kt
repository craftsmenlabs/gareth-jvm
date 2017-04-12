package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines

interface GluelineValidator {
    fun validateGluelines(experiment: Gluelines): Boolean
    fun gluelineIsValid(type: GlueLineType, glueline: String?): Boolean
}
