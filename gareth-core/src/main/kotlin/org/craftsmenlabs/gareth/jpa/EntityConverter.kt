package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EntityConverter @Autowired constructor(val timeService: TimeService) {


    fun toEntity(dto: ExperimentTemplateDTO): ExperimentTemplateEntity {
        val entity = ExperimentTemplateEntity()
        entity.name = dto.name
        entity.dateCreated = timeService.now()
        entity.baseline = dto.glueLines.baseline
        entity.assume = dto.glueLines.assume
        entity.success = dto.glueLines.success
        entity.failure = dto.glueLines.failure
        entity.timeline = dto.glueLines.time
        return entity
    }

    fun toEntity(dto: ExperimentTemplateCreateDTO): ExperimentTemplateEntity {
        val entity = ExperimentTemplateEntity()
        entity.name = dto.name
        entity.dateCreated = timeService.now()
        entity.baseline = dto.glueLines.baseline
        entity.assume = dto.glueLines.assume
        entity.success = dto.glueLines.success
        entity.failure = dto.glueLines.failure
        entity.timeline = dto.glueLines.time
        return entity
    }


    fun copyEditableValues(entity: ExperimentEntity, experiment: Experiment): ExperimentEntity {
        val timing = experiment.timing
        entity.dateReady = timing.ready
        entity.dateStarted = timing.started
        entity.dateWaitingForBaseline = timing.waitingForBaseline
        entity.dateBaselineExecuted = timing.baselineExecuted
        entity.dateWaitingForAssume = timing.waitingForAssume
        entity.dateAssumeExecuted = timing.assumeExecuted
        entity.dateWaitingFinalizing = timing.waitingFinalizing
        entity.dateFinalizingExecuted = timing.finalizingExecuted
        entity.dateCompleted = timing.completed
        entity.result = experiment.results.status

        val environment: List<ExperimentEnvironmentItem> = experiment.environment.items.map {
            val item = ExperimentEnvironmentItem()
            item.key = it.key
            item.value = it.value
            item.itemType = it.itemType
            item.experiment = entity
            item
        }
        entity.environment = environment.toSet()
        return entity
    }

    fun toDTO(experiment: Experiment): ExperimentDTO {
        val dto = ExperimentDTO(
                id = experiment.id ?: throw IllegalStateException("Cannot convert Experiment with null ID to DTO"),
                name = experiment.name,
                created = experiment.timing.created,
                glueLines = experiment.glueLines.copy(),
                ready = experiment.timing.ready,
                started = experiment.timing.started,
                baselineExecuted = experiment.timing.baselineExecuted,
                completed = experiment.timing.completed,
                result = experiment.results.status,
                environment = experiment.environment)
        return dto;
    }

    fun toDTO(entity: ExperimentEntity): Experiment {
        val template = entity.template
        val gluelines = Gluelines(
                assume = template.assume,
                baseline = template.baseline,
                success = template.success,
                failure = template.failure,
                time = template.timeline)

        val timing = ExperimentTiming(
                created = entity.dateCreated!!,
                ready = entity.dateReady,
                started = entity.dateStarted,
                waitingForBaseline = entity.dateWaitingForBaseline,
                baselineExecuted = entity.dateBaselineExecuted,
                waitingForAssume = entity.dateWaitingForAssume,
                assumeExecuted = entity.dateAssumeExecuted,
                waitingFinalizing = entity.dateWaitingFinalizing,
                finalizingExecuted = entity.dateFinalizingExecuted,
                completed = entity.dateCompleted)
        val environmentItems = entity.environment.map { EnvironmentItem(it.key, it.value, it.itemType) }
        return Experiment(id = entity.id!!,
                name = template.name,
                value = 0,
                glueLines = gluelines,
                timing = timing,
                results = ExperimentResults(entity.result),
                environment = ExperimentRunEnvironment(environmentItems))
    }

    fun toDTO(entity: ExperimentTemplateEntity): ExperimentTemplateDTO {
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