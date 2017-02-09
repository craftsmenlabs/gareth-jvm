package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Profile("!test")
class JPAExperimentStorage @Autowired constructor(val converter: EntityConverter,
                                                  val dao: ExperimentDao,
                                                  val dateTimeService: TimeService) : ExperimentStorage {

    var saveListener: ((Experiment) -> Unit)? = null

    override fun getFiltered(createdAfter: LocalDateTime?, onlyFinished: Boolean?): List<Experiment> {
        val creationFilter: (ExperimentEntity) -> Boolean = { createdAfter == null || it.dateCreated.after(dateTimeService.toDate(createdAfter)) }
        val finishedFilter: (ExperimentEntity) -> Boolean = { onlyFinished == null || it.dateCompleted != null }
        return dao.findAll().filter { creationFilter.invoke(it) && finishedFilter.invoke(it) }.map { converter.toDTO(it) }
    }

    override fun loadAllExperiments(): List<Experiment> {
        return dao.findAll().map { converter.toDTO(it) }
    }

    override fun save(experiment: Experiment): Experiment {
        val entity = converter.toEntity(experiment)
        val savedEntity = dao.save(entity)
        val saved = converter.toDTO(savedEntity)
        if (saveListener != null) {
            saveListener!!.invoke(saved)
        }
        return saved;
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        saveListener = listener
    }

    override fun getById(id: Long): Experiment {
        val entity = dao.findOne(id) ?: throw IllegalArgumentException("No experiment found with id $id")
        return converter.toDTO(entity)
    }
}