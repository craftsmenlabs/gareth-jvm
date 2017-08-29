package org.craftsmenlabs.gareth.validator.services

import org.craftsmenlabs.gareth.validator.BadRequestException
import org.craftsmenlabs.gareth.validator.NotFoundException
import org.craftsmenlabs.gareth.validator.definitions.DefinitionValidator
import org.craftsmenlabs.gareth.validator.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentTemplateDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentTemplateUpdateDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.mongo.ExperimentDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateConverter
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateDao
import org.craftsmenlabs.gareth.validator.mongo.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TemplateService @Autowired constructor(private val templateDao: ExperimentTemplateDao,
                                             private val experimentDao: ExperimentDao,
                                             private val definitionValidator: DefinitionValidator,
                                             private val timeService: TimeService,
                                             private val templateConverter: ExperimentTemplateConverter) {

    private val log = LoggerFactory.getLogger(TemplateService::class.java)

    fun update(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
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
        if (dto.archived != null)
            entity.archived = dto.archived!!
        if (dto.interval != null)
            entity.interval = dto.interval!!
        if (gluelinesHaveChanged(dto)) {
            val isReady = getChangedGluelines(dto).all { definitionValidator.gluelineIsValid(entity.projectId, it.key, it.value) }
            entity.ready = if (isReady) timeService.now() else null
        }
        val saved = templateDao.save(entity)
        return templateConverter.toDTO(saved)
    }

    fun create(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        val existing = templateDao.findByName(dto.name)
        if (existing != null)
            throw BadRequestException("Cannot create template '${dto.name}': name exists")
        val entity = templateConverter.toEntity(dto)
        val isReady = definitionValidator.validateGluelines(dto.projectid, dto.glueLines)
        if (isReady) {
            log.info("Experiment gluelines are valid.")
            entity.ready = timeService.now()
        } else {
            log.warn("Experiment gluelines are not ready! You must update this template before you can schedule experiment runs.")
        }
        val saved = templateDao.save(entity)
        return templateConverter.toDTO(saved)
    }

    fun getTemplateById(id: String): ExperimentTemplateDTO {
        return templateConverter.toDTO(findTemplateById(id))
    }

    fun getFiltered(name: String?): List<ExperimentTemplateDTO> {
        if (name != null) {
            val entity = templateDao.findByName(name) ?: throw IllegalArgumentException("No template named $name")
            return listOf(templateConverter.toDTO(entity))
        } else return templateDao.findAll().map { templateConverter.toDTO(it) }
    }

    private fun validateNoRunningExperimentsForTemplate(entity: ExperimentTemplateEntity) {
        val experiments = experimentDao.findByTemplateId(entity.id!!)
        if (!experiments.isEmpty())
            throw BadRequestException("You cannot update experiment template ${entity.name}. There are already running experiments.")
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

    private fun findTemplateById(id: String) = templateDao.findOne(id) ?: throw NotFoundException("No template with id $id")

    fun findByid(templateId: String): ExperimentTemplateEntity =
            templateDao.findOne(templateId) ?: throw IllegalArgumentException("No template with id $templateId")

}