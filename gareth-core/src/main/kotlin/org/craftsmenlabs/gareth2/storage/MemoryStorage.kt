package org.craftsmenlabs.gareth2.storage

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MemoryStorage : ExperimentStorage {

    override fun getById(id: Long): Experiment {
        return cache[id] ?: throw IllegalArgumentException("Not a valid id $id")
    }

    val log = LoggerFactory.getLogger("MemoryStorage")
    val cache: MutableMap<Long, Experiment> = mutableMapOf()

    var saveListener: ((Experiment) -> Unit)? = null

    override fun loadAllExperiments(): List<Experiment> {
        return cache.values.toList()
    }

    override fun save(experiment: Experiment): Experiment {
        val withId =
                if (experiment.id == null) experiment.copy(id = MemoryStorage.nextID()) else experiment
        cache[withId.id!!] = withId
        log.info("saved experiment ${withId.id}")
        if (saveListener != null) {
            saveListener!!.invoke(withId)
        }
        return withId
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        saveListener = listener
    }

    companion object {
        var ID: Long = 1
        fun nextID(): Long = ++ID
    }
}