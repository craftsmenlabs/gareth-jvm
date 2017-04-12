package org.craftsmenlabs.gareth.validator.client

import org.craftsmenlabs.gareth.validator.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines

interface GluelineValidator {

    fun gluelineIsValid(projectId: String, type: GlueLineType, glueline: String?): Boolean

    fun validateGluelines(projectId: String, experiment: Gluelines): Boolean

    fun lookupGlueline(projectId: String, type: GlueLineType, content: String): GlueLineSearchResultDTO
}
