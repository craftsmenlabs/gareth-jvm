package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment


interface ExperimentStorage {
    fun loadAllExperiments(): List<Experiment>
    fun save(experiment: Experiment)
    fun setListener(listener: ((Experiment) -> Unit)?)
}