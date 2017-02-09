package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.Experiment

interface GlueLineLookup {
    fun isExperimentReady(experiment: Experiment):Boolean
}
