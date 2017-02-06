package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth2.GlueLineLookup
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("mock")
open class WrappedGlueLineLookup : GlueLineLookup {

    lateinit var mock: GlueLineLookup

    override fun isExperimentReady(experiment: Experiment): Boolean {
        return mock!!.isExperimentReady(experiment)
    }
}