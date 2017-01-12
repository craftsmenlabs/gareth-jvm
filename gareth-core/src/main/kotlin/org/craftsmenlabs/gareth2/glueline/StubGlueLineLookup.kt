package org.craftsmenlabs.gareth2.glueline

import org.craftsmenlabs.gareth2.GlueLineLookup
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("stub")
open class StubGlueLineLookup : GlueLineLookup {
    override fun isExperimentReady(experiment: Experiment): Boolean {
        return true
    }
}
