package org.craftsmenlabs.gareth.validator.monitors

import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentLifecycle
import org.craftsmenlabs.gareth.validator.providers.ExperimentProvider
import org.craftsmenlabs.gareth.validator.services.ExperimentExecutor
import org.craftsmenlabs.gareth.validator.services.ExperimentService
import org.craftsmenlabs.gareth.validator.time.DurationCalculator
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import java.util.concurrent.TimeUnit

@Service
class ExecuteAssumeMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentService: ExperimentService,
        private val experimentExecutor: ExperimentExecutor,
        private val durationCalculator: DurationCalculator)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentService, ExperimentLifecycle.BASELINE_EXECUTED) {
    private val delayedExperiments = mutableListOf<String>()

    override fun extend(observable: Observable<ExperimentDTO>): Observable<ExperimentDTO> {
        return observable
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val delayInSeconds = durationCalculator.getDifferenceInSeconds(
                            dateTimeService.now(),
                            it.baselineExecuted,
                            durationCalculator.getDuration(it))
                    delayedExperiments.add(it.id)
                    val delay = Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                    return@delay delay
                }
                .map {
                    val finalizedExperiment = experimentExecutor.executeAssume(it)
                    delayedExperiments.remove(finalizedExperiment.id)
                    finalizedExperiment
                }
    }

}
