package org.craftsmenlabs.gareth.validator.services

import org.craftsmenlabs.gareth.validator.BadRequestException
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.mongo.ExperimentConverter
import org.craftsmenlabs.gareth.validator.mongo.ExperimentDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentEntity
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.validator.time.DateFormatUtils
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentService @Autowired constructor(val experimentDao: ExperimentDao,
                                               val templateService: TemplateService,
                                               val converter: ExperimentConverter,
                                               val timeService: TimeService) {
    var saveListener: ((ExperimentDTO) -> Unit)? = null

    fun setListener(listener: ((ExperimentDTO) -> Unit)?) {
        saveListener = listener
    }

    fun getExperimentById(id: String): ExperimentDTO {
        return converter.toDTO(findById(id))
    }

    fun getFiltered(projectId: String,
                    createdAfter: String? = null,
                    status: ExecutionStatus? = null,
                    completed: Boolean?): List<ExperimentDTO> =
            getFilteredEntities(projectId, createdAfter, status, completed).map { converter.toDTO(it) }


    private fun validateFilterCriteria(status: ExecutionStatus? = null,
                                       onlyFinished: Boolean?) {
        if (onlyFinished != null) {
            if (onlyFinished == true && status != null && !status.isCompleted())
                throw IllegalArgumentException("Cannot filter on running status when querying only for finished experiments.")
        }
    }

    fun getFilteredEntities(projectId: String,
                            createdAfterStr: String? = null,
                            status: ExecutionStatus? = null,
                            onlyFinished: Boolean? = null): List<ExperimentEntity> {
        validateFilterCriteria(status, onlyFinished)
        val createdAfter = if (createdAfterStr == null) null else DateFormatUtils.parseDateStringToMidnight(createdAfterStr)
        val creationFilter: (ExperimentEntity) -> Boolean = {
            createdAfter == null || it.dateCreated.isAfter(createdAfter)
        }
        val finishedFilter: (ExperimentEntity) -> Boolean = {
            onlyFinished == null || onlyFinished == (it.dateCompleted != null)
        }
        val statusFilter: (ExperimentEntity) -> Boolean = {
            status == null || status == it.result
        }
        return experimentDao.findByProjectId(projectId)
                .filter(creationFilter)
                .filter(finishedFilter)
                .filter(statusFilter)
    }

    fun createExperiment(dto: ExperimentCreateDTO): ExperimentDTO {
        val entity = createEntityForTemplate(dto.templateId)
        val now = timeService.now()
        entity.dateCreated = now
        entity.dateDue = if (dto.dueDate == null) now else timeService.toDate(dto.dueDate as DateTimeDTO)
        //template is ready
        entity.environment = if (dto.environment == null) setOf() else converter.getEnvironmentItems(dto.environment!!).toSet()
        entity.result = ExecutionStatus.PENDING
        val saved = experimentDao.save(entity)
        val dto = converter.toDTO(saved)
        saveListener?.invoke(dto)
        return dto
    }

    private fun createEntityForTemplate(templateId: String): ExperimentEntity {
        val template: ExperimentTemplateEntity = templateService.findByid(templateId)
        if (template.ready == null) {
            throw BadRequestException("You cannot start an experiment that is not ready.")
        }
        val entity = ExperimentEntity()
        entity.projectId = template.projectId
        entity.templateId = templateId
        //When the template is not ready this code is never reached, hence these exceptions should never be thrown
        entity.baseline = template.baseline ?: throw IllegalStateException("baseline cannot be null")
        entity.assume = template.assume ?: throw IllegalStateException("assume cannot be null")
        entity.timeline = template.timeline ?: throw IllegalStateException("timeline cannot be null")
        entity.success = template.success
        entity.failure = template.failure
        entity.name = template.name
        return entity
    }

    fun updateExperiment(experiment: ExperimentDTO): ExperimentDTO {
        val updated = converter.copyEditableValues(findById(experiment.id), experiment)
        val savedEntity = experimentDao.save(updated)
        val dto = converter.toDTO(savedEntity)
        saveListener?.invoke(dto)
        return dto
    }

    fun loadAllExperiments(): List<ExperimentDTO> {
        return experimentDao.findAll().map { converter.toDTO(it) }
    }

    /**
     * Bases on the template's interval pattern, returns the creation dto for the next experiment,
     * or null if the template has no repeat
     */
    fun scheduleNewInstance(dto: ExperimentDTO): ExperimentCreateDTO? {
        val template = templateService.findByid(findById(dto.id).templateId)
        if (template.interval != ExecutionInterval.NO_REPEAT) {
            val delay = timeService.getDelay(timeService.now(), template.interval)
            return ExperimentCreateDTO(templateId = template.id!!, dueDate = DateTimeDTO(delay), environment = dto.environment)
        } else
            return null
    }

    fun findById(id: String): ExperimentEntity =
            experimentDao.findOne(id) ?: throw IllegalArgumentException("No experiment with id $id")

}