package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment

interface GluelineLookup {
    fun isExperimentReady(experiment: Experiment):Boolean
    fun isLineReady(glueline : String):Boolean
}
