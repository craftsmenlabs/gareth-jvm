package org.craftsmenlabs.gareth.mongo

import org.craftsmenlabs.BadRequestException
import org.craftsmenlabs.GarethIllegalDefinitionException
import org.craftsmenlabs.NotFoundException
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.client.GluelineValidatorRestClient
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MongoStorage @Autowired constructor(private val converter: MongoEntityConverter,
                                          private val dao: MongoExperimentDao,
                                          private val templateDao: MongoExperimentTemplateDao,
                                          private val glueLineLookupRestClient: GluelineValidatorRestClient,
                                          private val timeService: TimeService) : ExperimentStorage {

    override fun getAllTemplates(): List<ExperimentTemplateDTO> {
        return templateDao.findAll().map { converter.toDTO(it) }
    }

    override fun getTemplateById(id: String): ExperimentTemplateDTO {
        return converter.toDTO(findTemplateById(id))
    }

    override fun createTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        val existing = templateDao.findByName(dto.name)
        if (!existing.isEmpty())
            throw BadRequestException("Cannot create template '${dto.name}': name exists")
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
        if (gluelinesHaveChanged(dto)) {
            val isReady = validateGluelinesForUpdate(dto)
            entity.ready = if (isReady) timeService.now() else null
        }
        templateDao.save(entity)
        return converter.toDTO(entity)
    }

    private fun validateNoRunningExperimentsForTemplate(entity: MongoExperimentTemplateEntity) {
        val experiments = dao.findByTemplateId(entity.id!!)
        if (!experiments.isEmpty())
            throw BadRequestException("You cannot update experiment template ${entity.name}. There are already running experiments.")
    }

    private fun validateGluelinesForUpdate(dto: ExperimentTemplateUpdateDTO): Boolean {
        return getChangedGluelines(dto).all { glueLineLookupRestClient.gluelineIsValid(it.key, it.value) }
    }

    private fun save(entity: MongoExperimentTemplateEntity): ExperimentTemplateDTO {
        val saved = templateDao.save(entity)
        return converter.toDTO(saved)
    }


    override fun createExperiment(templateId: String, startDate: LocalDateTime?): Experiment {
        val entity = createEntityForTemplate(templateId)
        val now = timeService.now()
        entity.dateCreated = now
        //can be null
        entity.dateDue = if (startDate == null) now else startDate
        //template is ready
        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING

        val savedEntity = dao.save(entity)
        val saved = converter.toDTO(savedEntity)
        saveListener?.invoke(saved)
        return saved
    }

    private fun createEntityForTemplate(templateId: String): MongoExperimentEntity {
        val template: MongoExperimentTemplateEntity = findTemplateById(templateId)
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

    var saveListener: ((Experiment) -> Unit)? = null

    private fun findTemplateById(id: String) = templateDao.findOne(id) ?: throw NotFoundException("No template with id $id")

    override fun getFiltered(createdAfter: LocalDateTime?, onlyFinished: Boolean?): List<Experiment> {
        val creationFilter: (MongoExperimentEntity) -> Boolean = {
            createdAfter == null || it.dateCreated.isAfter(createdAfter)
        }
        val finishedFilter: (MongoExperimentEntity) -> Boolean = {
            onlyFinished == null || onlyFinished == (it.dateCompleted != null)
        }
        return dao.findAll().filter { creationFilter.invoke(it) && finishedFilter.invoke(it) }.map { converter.toDTO(it) }
    }

    override fun loadAllExperiments(): List<Experiment> {
        return dao.findAll().map { converter.toDTO(it) }
    }

    override fun updateExperiment(experiment: Experiment): Experiment {
        val entity = dao.findOne(experiment.id) ?: throw NotFoundException("No experiment found with id ${experiment.id}")
        val updated = converter.copyEditableValues(entity, experiment)
        val savedEntity = dao.save(updated)
        val saved = converter.toDTO(savedEntity)
        saveListener?.invoke(saved)
        return saved
    }

    override fun setListener(listener: ((Experiment) -> Unit)?) {
        saveListener = listener
    }

    override fun getById(id: String): Experiment {
        val entity = dao.findOne(id) ?: throw NotFoundException("No experiment found with id $id")
        return converter.toDTO(entity)
    }

    fun gluelinesHaveChanged(dto: ExperimentTemplateUpdateDTO) = !getChangedGluelines(dto).isEmpty()

    fun getChangedGluelines(dto: ExperimentTemplateUpdateDTO): Map<GlueLineType, String> {
        val changed = mutableMapOf<GlueLineType, String>()
        if (dto.baseline != null)
            changed.put(GlueLineType.BASELINE, dto.baseline as String)
        if (dto.assume != null)
            changed.put(GlueLineType.ASSUME, dto.assume as String)
        if (dto.success != null)
            changed.put(GlueLineType.SUCCESS, dto.success as String)
        if (dto.failure != null)
            changed.put(GlueLineType.FAILURE, dto.failure as String)
        if (dto.time != null)
            changed.put(GlueLineType.TIME, dto.time as String)
        return changed
    }

    fun getTemplateByName(name: String): ExperimentTemplateDTO {
        val templates = templateDao.findByName(name)
        if (templates.isEmpty())
            throw NotFoundException("No template with name $name")
        else if (templates.size > 1)
            throw GarethIllegalDefinitionException("Cannot have more than one template with name $name")
        return converter.toDTO(templates[0])
    }
}
