package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.Experiment


interface ExperimentStorage {
    fun loadAllExperiments(): List<Experiment>
    fun save(experiment: Experiment): Experiment
    fun setListener(listener: ((Experiment) -> Unit)?)
    fun getById(id: Long): Experiment
}