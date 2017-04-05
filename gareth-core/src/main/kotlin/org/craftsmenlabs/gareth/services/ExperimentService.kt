package org.craftsmenlabs.gareth.services

import org.craftsmenlabs.BadRequestException
import org.craftsmenlabs.NotFoundException
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.mongo.*
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentService @Autowired constructor(val experimentDao: MongoExperimentDao,
                                               val templateDao: MongoExperimentTemplateDao,
                                               val converter: ExperimentConverter,
                                               val timeService: TimeService) {
    var saveListener: ((ExperimentDTO) -> Unit)? = null

    fun setListener(listener: ((ExperimentDTO) -> Unit)?) {
        saveListener = listener
    }

    fun getExperimentById(id: String): ExperimentDTO {
        val entity = experimentDao.findOne(id) ?: throw NotFoundException("No experiment found with id $id")
        return converter.toDTO(entity)
    }

    fun getFiltered(ddMMYYYY: String?,
                    onlyFinished: Boolean?): List<ExperimentDTO> {
        val createdAfter = if (ddMMYYYY == null) null else DateFormatUtils.parseDateStringToMidnight(ddMMYYYY)
        val creationFilter: (MongoExperimentEntity) -> Boolean = {
            createdAfter == null || it.dateCreated.isAfter(createdAfter)
        }
        val finishedFilter: (MongoExperimentEntity) -> Boolean = {
            onlyFinished == null || onlyFinished == (it.dateCompleted != null)
        }
        return experimentDao.findAll().filter { creationFilter.invoke(it) && finishedFilter.invoke(it) }.map { converter.toDTO(it) }
    }

    fun createExperiment(dto: ExperimentCreateDTO): ExperimentDTO {
        val entity = createEntityForTemplate(dto.templateId)
        val now = timeService.now()
        entity.dateCreated = now
        entity.dateDue = dto.dueDate ?: now
        //template is ready
        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING
        val saved = experimentDao.save(entity)
        val dto = converter.toDTO(saved)
        saveListener?.invoke(dto)
        return dto
    }

    private fun createEntityForTemplate(templateId: String): MongoExperimentEntity {
        val template: MongoExperimentTemplateEntity = templateDao.findOne(templateId)
        if (template.ready == null) {
            throw BadRequestException("You cannot start an experiment that is not ready.")
        }
        val entity = MongoExperimentEntity()
        entity.templateId = templateId
        entity.baseline = template.baseline
        entity.assume = template.assume
        entity.success = template.success
        entity.failure = template.failure
        entity.timeline = template.timeline
        entity.name = template.name
        return entity
    }

    fun updateExperiment(experiment: ExperimentDTO): ExperimentDTO {
        val entity = experimentDao.findOne(experiment.id) ?: throw NotFoundException("No experiment found with id ${experiment.id}")
        val updated = converter.copyEditableValues(entity, experiment)
        val savedEntity = experimentDao.save(updated)
        val dto = converter.toDTO(savedEntity)
        saveListener?.invoke(dto)
        return dto
    }

    fun loadAllExperiments(): List<ExperimentDTO> {
        return experimentDao.findAll().map { converter.toDTO(it) }
    }
}