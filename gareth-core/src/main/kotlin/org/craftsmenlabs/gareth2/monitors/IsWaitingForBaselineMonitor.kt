package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.providers.state.StartedExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class IsWaitingForBaselineMonitor @Autowired constructor(
        startedExperimentProvider: StartedExperimentProvider,
        experimentStorage: ExperimentStorage) {

    init {
        startedExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { it.timing.waitingForBaseline = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
