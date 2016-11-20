package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.providers.state.RunningExperimentProvider
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
        runningExperimentProvider: RunningExperimentProvider,
        durationCalculator: DurationCalculator,
        experimentStorage: ExperimentStorage) {

    init {
        runningExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .delay {
                    val duration = durationCalculator.getDuration(it)
                    val assumePlanned = it.timing.baselineExecuted?.plus(duration)
                    val delayInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), assumePlanned)
                    return@delay Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                }
                .map { it.apply { it.timing.waitingForAssume = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
