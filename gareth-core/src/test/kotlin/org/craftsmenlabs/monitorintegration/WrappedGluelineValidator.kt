package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth.model.Gluelines
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("mock")
open class WrappedGluelineValidator : GluelineValidator {

    lateinit var mock: GluelineValidator

    override fun gluelineIsValid(type: GlueLineType, glueline: String): Boolean {
        return mock!!.gluelineIsValid(type, glueline)
    }

    override fun validateGluelines(gluelines: Gluelines): Boolean {
        return mock!!.validateGluelines(gluelines)
    }
}
