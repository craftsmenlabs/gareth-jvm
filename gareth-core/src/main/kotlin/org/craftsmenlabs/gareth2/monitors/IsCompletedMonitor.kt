package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Service
class IsCompletedMonitor @Autowired constructor(
        private val experimentProvider: ExperimentProvider,
        private val dateTimeService: DateTimeService,
        private val experimentStorage: ExperimentStorage) {

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { it.getState() == ExperimentState.FINALISATION_EXECUTED }
                .map { it.apply { it.timing.completed = dateTimeService.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
