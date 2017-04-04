package org.craftsmenlabs.gareth.mongo

import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MongoEntityConverter @Autowired constructor(val timeService: TimeService) {
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


    fun copyEditableValues(entity: MongoExperimentEntity, experiment: Experiment): MongoExperimentEntity {
        val timing = experiment.timing
        entity.dateDue = timing.due
        entity.dateBaselineExecuted = timing.baselineExecuted
        entity.dateCompleted = timing.completed
        entity.result = experiment.status

        val environment: List<ExperimentEnvironmentItem> = experiment.environment.items.map {
            val item = ExperimentEnvironmentItem()
            item.key = it.key
            item.value = it.value
            item.itemType = it.itemType
            item
        }
        entity.environment = environment.toSet()
        return entity
    }

    fun toDTO(experiment: Experiment): ExperimentDTO {
        val dto = ExperimentDTO(
                id = experiment.id,
                name = experiment.name,
                created = experiment.timing.created,
                glueLines = experiment.glueLines.copy(),
                due = experiment.timing.due,
                baselineExecuted = experiment.timing.baselineExecuted,
                completed = experiment.timing.completed,
                result = experiment.status,
                environment = experiment.environment)
        return dto;
    }

    fun toDTO(entity: MongoExperimentEntity): Experiment {
        val gluelines = Gluelines(
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline)

        val timing = ExperimentTiming(
                created = entity.dateCreated,
                due = entity.dateDue,
                baselineExecuted = entity.dateBaselineExecuted,
                completed = entity.dateCompleted)
        val environmentItems = entity.environment.map { EnvironmentItem(it.key, it.value, it.itemType) }
        return Experiment(id = entity.id.toString(),
                name = entity.name,
                value = 0,
                glueLines = gluelines,
                timing = timing,
                status = entity.result,
                environment = ExperimentRunEnvironment(environmentItems))
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