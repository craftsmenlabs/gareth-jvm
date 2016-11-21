package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Service
class IsWaitingForAssumeMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        durationCalculator: DurationCalculator,
        experimentStorage: ExperimentStorage) {

    private val delayedExperiments = mutableListOf<String>()

    init {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { it.getState() == ExperimentState.BASELINE_EXECUTED }
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val duration = durationCalculator.getDuration(it)
                    val assumePlanned = it.timing.baselineExecuted?.plus(duration)
                    val delayInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), assumePlanned)
                    delayedExperiments.add(it.id)
                    return@delay Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                }
                .map { it.apply { it.timing.waitingForAssume = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe {
                    experimentStorage.save(it)
                    delayedExperiments.remove(it.id)
                }
    }
}
