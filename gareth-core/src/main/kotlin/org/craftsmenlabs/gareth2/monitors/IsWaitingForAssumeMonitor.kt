package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Service
class IsWaitingForAssumeMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: DateTimeService,
        experimentStorage: ExperimentStorage,
        private val durationCalculator: DurationCalculator)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.BASELINE_EXECUTED) {

    private val delayedExperiments = mutableListOf<Long>()

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val duration = durationCalculator.getDuration(it)
                    val assumePlanned = it.timing.baselineExecuted?.plus(duration)
                    val now = dateTimeService.now()
                    val delayInSeconds = ChronoUnit.SECONDS.between(now, assumePlanned)
                    delayedExperiments.add(it.id!!)
                    val delay = Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                    return@delay delay
                }
                .map { it.copy(timing = it.timing.copy(waitingForAssume = dateTimeService.now())) }
    }
}

