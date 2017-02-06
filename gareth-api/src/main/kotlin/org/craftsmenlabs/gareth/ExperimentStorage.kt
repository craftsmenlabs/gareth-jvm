package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.Experiment
import java.time.LocalDateTime


interface ExperimentStorage {
    fun loadAllExperiments(): List<Experiment>
    fun save(experiment: Experiment): Experiment
    fun setListener(listener: ((Experiment) -> Unit)?)
    fun getById(id: Long): Experiment
    fun getFiltered(createdAfter: LocalDateTime? = null, onlyFinished: Boolean? = false): List<Experiment>
}