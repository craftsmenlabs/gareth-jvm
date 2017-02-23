package org.craftsmenlabs.gareth.model

import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentDTOConverter constructor(@Autowired val dateTimeService: TimeService) {

    fun createExperiment(dto: ExperimentCreateDTO): Experiment {
        val details = ExperimentDetails(
                name = dto.name,
                baseline = dto.baseline,
                assume = dto.assume,
                time = dto.time,
                success = dto.success,
                failure = dto.failure,
                value = dto.value)
        val experiment = Experiment(
                details = details,
                environment = dto.environment,
                timing = ExperimentTiming(dateTimeService.now()),
                results = ExperimentResults())
        return experiment
    }

    fun createDTO(experiment: Experiment): ExperimentDTO {
        val dto = ExperimentDTO(
                assume = experiment.details.assume,
                baseline = experiment.details.baseline,
                success = experiment.details.success,
                failure = experiment.details.failure,
                time = experiment.details.time,
                id = experiment.id!!,
                name = experiment.details.name,
                created = experiment.timing.created,
                ready = experiment.timing.ready,
                started = experiment.timing.started,
                baselineExecuted = experiment.timing.baselineExecuted,
                completed = experiment.timing.completed,
                result = experiment.results.status,
                environment = experiment.environment)
        return dto;
    }
}
