package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment

interface GlueLineLookup {
    fun isExperimentReady(experiment: Experiment):Boolean
}
