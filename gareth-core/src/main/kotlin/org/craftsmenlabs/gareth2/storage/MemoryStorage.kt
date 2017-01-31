package org.craftsmenlabs.gareth2.storage

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemoryStorage : ExperimentStorage {

    override fun getById(id: String): Experiment {
        return cache[id] ?: throw IllegalArgumentException("Not a valid id $id")
    }

    val log = LoggerFactory.getLogger("MemoryStorage")
    val cache: MutableMap<String, Experiment> = mutableMapOf()

    var saveListener: ((Experiment) -> Unit)? = null

    override fun loadAllExperiments(): List<Experiment> {
        return cache.values.toList()
    }

    override fun save(experiment: Experiment) {
        cache[experiment.id] = experiment
        log.info("saved experiment ${experiment.id}")
        if (saveListener != null) {
            saveListener!!.invoke(experiment)
        }
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        saveListener = listener
    }
}