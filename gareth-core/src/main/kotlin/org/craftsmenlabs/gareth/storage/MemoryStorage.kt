package org.craftsmenlabs.gareth.storage

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Profile("test")
class MemoryStorage : ExperimentStorage {


    val log = LoggerFactory.getLogger("MemoryStorage")
    val cache: MutableMap<Long, Experiment> = mutableMapOf()

    override fun getFiltered(createdAfter: LocalDateTime?, onlyFinished: Boolean?): List<Experiment> {
        val creationFilter: (Experiment) -> Boolean = { createdAfter == null || it.timing.created.isAfter(createdAfter) }
        val finishedFilter: (Experiment) -> Boolean = { onlyFinished == null || it.timing.completed != null }
        return cache.values.filter { creationFilter.invoke(it) && finishedFilter.invoke(it) }
    }

    override fun getById(id: Long): Experiment {
        return cache[id] ?: throw IllegalArgumentException("Not a valid id $id")
    }

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