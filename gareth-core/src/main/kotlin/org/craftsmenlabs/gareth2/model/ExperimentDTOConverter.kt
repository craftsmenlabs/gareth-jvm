package org.craftsmenlabs.gareth2.model

import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ExperimentDTOConverter constructor(@Autowired val dateTimeService: DateTimeService) {

    fun createExperiment(dto: ExperimentCreateDTO): Experiment {
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
                timing = ExperimentTiming(dateTimeService.now()),
                results = ExperimentResults())
        return experiment
    }

    fun createDTO(experiment: Experiment): ExperimentDTO {
        fun nullSafeDate(dt: LocalDateTime?): Date? = if (dt == null) null else dateTimeService.toDate(dt)

        val dto = ExperimentDTO(
                assume = experiment.details.assume,
                baseline = experiment.details.baseline,
                success = experiment.details.success,
                failure = experiment.details.failure,
                time = experiment.details.time,
                id = experiment.id!!,
                name = experiment.details.name,
                created = nullSafeDate(experiment.timing.created) ?: throw IllegalArgumentException("create date is mandatory"),
                ready = nullSafeDate(experiment.timing.ready),
                started = nullSafeDate(experiment.timing.started),
                baselineExecuted = nullSafeDate(experiment.timing.baselineExecuted),
                completed = nullSafeDate(experiment.timing.completed),
                environment = experiment.environment)
        return dto;
    }

}