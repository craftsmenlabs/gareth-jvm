package org.craftsmenlabs.gareth.validator

import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines

interface GluelineValidator {
    fun validateGluelines(experiment: Gluelines): Boolean
    fun gluelineIsValid(type: GlueLineType, glueline: String?): Boolean
}
