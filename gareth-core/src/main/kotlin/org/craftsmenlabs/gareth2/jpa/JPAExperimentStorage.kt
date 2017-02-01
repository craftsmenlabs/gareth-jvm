package org.craftsmenlabs.gareth2.jpa

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class JPAExperimentStorage : ExperimentStorage {

    @Autowired
    private lateinit var converter: EntityConverter
    @Autowired
    private lateinit var dao: ExperimentDao

    var saveListener: ((Experiment) -> Unit)? = null
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