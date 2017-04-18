package org.craftsmenlabs.gareth.validator.monitors

import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentLifecycle
import org.craftsmenlabs.gareth.validator.providers.ExperimentProvider
import org.craftsmenlabs.gareth.validator.services.ExperimentExecutor
import org.craftsmenlabs.gareth.validator.services.ExperimentService
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import java.util.concurrent.TimeUnit

@Service
class ExecuteBaselineMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentService: ExperimentService,
        private val experimentExecutor: ExperimentExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentService, ExperimentLifecycle.NEW) {
    private val delayedExperiments = mutableListOf<String>()
    override fun extend(observable: Observable<ExperimentDTO>): Observable<ExperimentDTO> {
        return observable
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val delayInSeconds = dateTimeService.getSecondsUntil(it.due)
                    log.info("Waiting $delayInSeconds seconds until executing baseline of experiment ${it.id}")
                    delayedExperiments.add(it.id)
                    val delay = Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                    return@delay delay
                }
                .map {
                    scheduleNextExperimentRun(experimentExecutor.executeBaseline(it))
                }
    }

    private fun scheduleNextExperimentRun(dto: ExperimentDTO): ExperimentDTO {
        val createDTO = experimentService.scheduleNewInstance(dto)
        if (createDTO != null)
            experimentService.createExperiment(createDTO)
        return dto
    }
}
