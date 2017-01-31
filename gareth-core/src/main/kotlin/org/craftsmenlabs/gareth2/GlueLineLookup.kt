package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth.model.Experiment

interface GlueLineLookup {
    fun isExperimentReady(experiment: Experiment):Boolean
}
