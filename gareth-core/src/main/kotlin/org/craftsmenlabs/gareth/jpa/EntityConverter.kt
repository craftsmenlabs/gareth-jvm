package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.*
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class EntityConverter {

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

    private fun toDate(dateTime: LocalDateTime?): Timestamp? {
        if (dateTime == null)
            return null
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Timestamp.from(instant)
    }

    private fun toLocalDateTime(timeStamp: Timestamp?): LocalDateTime? {
        if (timeStamp == null)
            return null
        val instant = Instant.ofEpochMilli(timeStamp.getTime())
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }
}