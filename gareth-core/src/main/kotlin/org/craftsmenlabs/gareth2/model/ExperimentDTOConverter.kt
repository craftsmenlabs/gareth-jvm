package org.craftsmenlabs.gareth2.model

import org.craftsmenlabs.gareth.api.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.api.model.ExperimentDTO
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Service
class ExperimentDTOConverter constructor(@Autowired val dateTimeService: DateTimeService) {

    fun createExperiment(dto: ExperimentCreateDTO): Experiment {
        val id = UUID.randomUUID().toString()
        val details = ExperimentDetails(
                name = dto.name,
                baseline = dto.baseline,
                assume = dto.assume,
                time = dto.time,
                success = dto.success,
                failure = dto.failure,
                value = dto.weight)
        val experiment = Experiment(
                details = details,
                id = id,
                timing = ExperimentTiming(dateTimeService.now()),
                results = ExperimentResults(false))
        return experiment
    }

    fun createDTO(experiment: Experiment): ExperimentDTO {
        val dto = ExperimentDTO()
        dto.assume = experiment.details.assume
        dto.baseline = experiment.details.baseline
        dto.success = experiment.details.success
        dto.failure = experiment.details.failure
        dto.time = experiment.details.time
        dto.id = experiment.id
        dto.name = experiment.details.name

        fun nullSafeDate(dt: LocalDateTime?): Date? = if (dt == null) null else dateTimeService.toDate(dt)

        dto.created = nullSafeDate(experiment.timing.created)
        dto.ready = nullSafeDate(experiment.timing.ready)
        dto.started = nullSafeDate(experiment.timing.started)
        dto.baselineExecuted = nullSafeDate(experiment.timing.baselineExecuted)
        dto.completed = nullSafeDate(experiment.timing.completed)

        dto.environment = mutableMapOf()
        experiment.environment.forEach { dto.environment[it.key] = it.value as Serializable }
        return dto;
    }

}