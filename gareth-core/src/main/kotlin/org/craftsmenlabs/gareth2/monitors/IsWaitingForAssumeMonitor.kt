package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.time.DurationCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import rx.schedulers.Schedulers
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class IsWaitingForAssumeMonitor @Autowired constructor(
        private val experimentProvider: ExperimentProvider,
        private val dateTimeService: DateTimeService,
        private val durationCalculator: DurationCalculator,
        private val experimentStorage: ExperimentStorage) {

    private val delayedExperiments = mutableListOf<String>()

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { it.getState() == ExperimentState.BASELINE_EXECUTED }
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val duration = durationCalculator.getDuration(it)
                    val assumePlanned = it.timing.baselineExecuted?.plus(duration)
                    val delayInSeconds = ChronoUnit.SECONDS.between(dateTimeService.now(), assumePlanned)
                    delayedExperiments.add(it.id)
                    return@delay Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                }
                .map { it.apply { it.timing.waitingForAssume = dateTimeService.now() } }
                .observeOn(Schedulers.computation())
                .subscribe {
                    experimentStorage.save(it)
                    delayedExperiments.remove(it.id)
                }
    }
}
