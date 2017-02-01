package org.craftsmenlabs.gareth2.jpa

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
        entity.dateCreated = toDate(timing.created)!!
        entity.dateReady = toDate(timing.ready)
        entity.dateStarted = toDate(timing.started)
        entity.dateWaitingForBaseline = toDate(timing.waitingForBaseline)
        entity.dateBaselineExecuted = toDate(timing.baselineExecuted)
        entity.dateWaitingForAssume = toDate(timing.waitingForAssume)
        entity.dateAssumeExecuted = toDate(timing.assumeExecuted)
        entity.dateWaitingFinalizing = toDate(timing.waitingFinalizing)
        entity.dateFinalizingExecuted = toDate(timing.finalizingExecuted)
        entity.dateCompleted = toDate(timing.completed)
        entity.result = experiment.results.status

        val environment: List<ExperimentEnvironmentItem> = experiment.environment.items.map {
            val item = ExperimentEnvironmentItem()
            item.key = it.key
            item.value = it.value
            item.itemType = it.itemType
            item
        }
        entity.environment = environment
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
                created = toLocalDateTime(entity.dateCreated)!!,
                ready = toLocalDateTime(entity.dateReady),
                started = toLocalDateTime(entity.dateStarted),
                waitingForBaseline = toLocalDateTime(entity.dateWaitingForBaseline),
                baselineExecuted = toLocalDateTime(entity.dateBaselineExecuted),
                waitingForAssume = toLocalDateTime(entity.dateWaitingForAssume),
                assumeExecuted = toLocalDateTime(entity.dateAssumeExecuted),
                waitingFinalizing = toLocalDateTime(entity.dateWaitingFinalizing),
                finalizingExecuted = toLocalDateTime(entity.dateFinalizingExecuted),
                completed = toLocalDateTime(entity.dateCompleted))
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