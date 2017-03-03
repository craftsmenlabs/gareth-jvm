package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.*
import org.springframework.stereotype.Service

@Service
class EntityConverter {

    fun toEntity(dto: ExperimentTemplate): ExperimentTemplateEntity {
        val entity = ExperimentTemplateEntity(dto.id)
        entity.id = dto.id
        entity.name = dto.name
        entity.assume = dto.assume
        entity.baseline = dto.baseline
        entity.success = dto.success
        entity.failure = dto.failure
        entity.timeline = dto.timeline
        return entity
    }

    fun toEntity(experiment: Experiment): ExperimentEntity {
        val entity = ExperimentEntity()
        entity.id = experiment.id
        entity.name = experiment.details.name
        entity.assume = experiment.details.assume
        entity.baseline = experiment.details.baseline
        entity.success = experiment.details.success
        entity.failure = experiment.details.failure
        entity.timeline = experiment.details.time
        val timing = experiment.timing
        entity.dateCreated = timing.created!!
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

    fun toDTO(entity: ExperimentEntity): Experiment {
        val details = ExperimentDetails(name = entity.name,
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline,
                value = 0)

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
                details = details,
                timing = timing,
                results = ExperimentResults(entity.result),
                environment = ExperimentRunEnvironment(environmentItems))
    }

    fun toDTO(entity: ExperimentTemplateEntity): ExperimentTemplate {
        return ExperimentTemplate(id = entity.id ?: throw IllegalStateException("Cannot convert ExperimentTemplate with null ID to DTO"),
                name = entity.name, assume = entity.assume, baseline = entity.baseline, success = entity.success, failure = entity.failure, timeline = entity.timeline)
    }
}