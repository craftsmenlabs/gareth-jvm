package org.craftsmenlabs.gareth2.storage

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.stereotype.Service

@Service
class InMemoryStorage : ExperimentStorage {

    private val experiments = mutableListOf<Experiment>()
    private var listener: ((Experiment) -> Unit)? = null

    override fun loadAllExperiments(): List<Experiment> = experiments

    override fun save(experiment: Experiment) {
        val oldExp = experiments.find { it.id == experiment.id }
        experiments.remove(oldExp)
        experiments.add(experiment)
        listener?.invoke(experiment)
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        this.listener = listener;
    }
}
