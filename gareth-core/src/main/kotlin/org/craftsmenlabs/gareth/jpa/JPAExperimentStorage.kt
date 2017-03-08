package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.BadRequestException
import org.craftsmenlabs.gareth.client.GluelineValidatorRestClient
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class JPAExperimentStorage @Autowired constructor(private val converter: EntityConverter,
                                                  private val dao: ExperimentDao,
                                                  private val glueLineLookupRestClient: GluelineValidatorRestClient,
                                                  private val templateDao: ExperimentTemplateDao,
                                                  private val timeService: TimeService) : ExperimentStorage {

    override fun getAllTemplates(): List<ExperimentTemplateDTO> {
        return templateDao.findAll().map { converter.toDTO(it) }
    }

    override fun getTemplateById(id: Long): ExperimentTemplateDTO {
        return converter.toDTO(findTemplateById(id))
    }

    override fun createTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        val entity = converter.toEntity(dto)
        val isReady = glueLineLookupRestClient.gluelinesAreValid(dto.glueLines)
        if (isReady)
            entity.ready = timeService.now()
        return save(entity)
    }

    override fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        val entity = findTemplateById(dto.id)
        validateNoRunningExperimentsForTemplate(entity)
        if (dto.name != null)
            entity.name = dto.name!!
        if (dto.baseline != null)
            entity.baseline = dto.baseline!!
        if (dto.assume != null)
            entity.assume = dto.assume!!
        if (dto.success != null)
            entity.success = dto.success!!
        if (dto.failure != null)
            entity.failure = dto.failure!!
        if (dto.time != null)
            entity.timeline = dto.time!!
        if (dto.gluelinesHaveChanged()) {
            val isReady = validateGluelinesForUpdate(dto)
            entity.ready = if (isReady) timeService.now() else null
        }
        templateDao.save(entity)
        return converter.toDTO(entity)
    }

    private fun validateNoRunningExperimentsForTemplate(entity: ExperimentTemplateEntity) {
        val experiments = dao.findByTemplate(entity)
        if (!experiments.isEmpty())
            throw BadRequestException("You cannot update experiment template ${entity.name}. There are already running experiments.")
    }

    private fun validateGluelinesForUpdate(dto: ExperimentTemplateUpdateDTO): Boolean {
        return dto.getChangedGluelines().all { glueLineLookupRestClient.gluelineIsValid(it.key, it.value) }
    }

    private fun save(entity: ExperimentTemplateEntity): ExperimentTemplateDTO {
        val saved = templateDao.save(entity)
        return converter.toDTO(saved)
    }


    override fun createExperiment(templateId: Long, startDate: LocalDateTime?): Experiment {
        val template = findTemplateById(templateId)
        if (template.ready == null) {
            throw BadRequestException("You cannot start an experiment that is not ready.")
        }
        val entity = ExperimentEntity()
        entity.template = template
        val now = timeService.now()
        entity.dateCreated = now
        //can be null
        entity.dateStarted = startDate
        //template is ready
        entity.dateReady = now
        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING
        return doSave(entity)
    }

    var saveListener: ((Experiment) -> Unit)? = null

    private fun findTemplateById(id: Long) = templateDao.findOne(id) ?: throw IllegalArgumentException("No template with id $id")

    override fun getFiltered(createdAfter: LocalDateTime?, onlyFinished: Boolean?): List<Experiment> {
        val creationFilter: (ExperimentEntity) -> Boolean = {
            createdAfter == null || it.dateCreated.isAfter(createdAfter)
        }
        val finishedFilter: (ExperimentEntity) -> Boolean = {
            onlyFinished == null || onlyFinished == (it.dateCompleted != null)
        }
        return dao.findAll().filter { creationFilter.invoke(it) && finishedFilter.invoke(it) }.map { converter.toDTO(it) }
    }

    override fun loadAllExperiments(): List<Experiment> {
        return dao.findAll().map { converter.toDTO(it) }
    }

    override fun updateExperiment(experiment: Experiment): Experiment {
        val entity = dao.findOne(experiment.id) ?: throw IllegalArgumentException("No experiment found with id ${experiment.id}")
        return doSave(converter.copyEditableValues(entity, experiment))
    }

    private fun doSave(entity: ExperimentEntity): Experiment {
        val savedEntity = dao.save(entity)
        val saved = converter.toDTO(savedEntity)
        saveListener?.invoke(saved)
        return saved
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        saveListener = listener
    }

    override fun getById(id: Long): Experiment {
        val entity = dao.findOne(id) ?: throw IllegalArgumentException("No experiment found with id $id")
        return converter.toDTO(entity)
    }

}