package org.craftsmenlabs.gareth.mongo

import org.craftsmenlabs.gareth.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateDTO
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentTemplateConverter @Autowired constructor(val timeService: TimeService) {

    fun toEntity(dto: ExperimentTemplateDTO): MongoExperimentTemplateEntity {
        val entity = MongoExperimentTemplateEntity()
        entity.name = dto.name
        entity.dateCreated = timeService.now()
        entity.baseline = dto.glueLines.baseline
        entity.assume = dto.glueLines.assume
        entity.success = dto.glueLines.success
        entity.failure = dto.glueLines.failure
        entity.timeline = dto.glueLines.time
        return entity
    }

    fun toEntity(dto: ExperimentTemplateCreateDTO): MongoExperimentTemplateEntity {
        val entity = MongoExperimentTemplateEntity()
        entity.name = dto.name
        entity.dateCreated = timeService.now()
        entity.baseline = dto.glueLines.baseline
        entity.assume = dto.glueLines.assume
        entity.success = dto.glueLines.success
        entity.failure = dto.glueLines.failure
        entity.timeline = dto.glueLines.time
        return entity
    }

    fun toDTO(entity: MongoExperimentTemplateEntity): ExperimentTemplateDTO {
        val glueLines = Gluelines(
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline)
        return ExperimentTemplateDTO(id = entity.id ?: throw IllegalStateException("Cannot convert ExperimentTemplate with null ID to DTO"),
                name = entity.name,
                created = entity.dateCreated,
                ready = entity.ready,
                glueLines = glueLines
        )
    }
}