package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentRun

interface ExperimentStorage {
    fun loadAllExperiments(): List<Experiment>
    fun loadAllRuns(): List<ExperimentRun>
    fun save(experiment: Experiment)
    fun save(experimentRun: ExperimentRun)
}