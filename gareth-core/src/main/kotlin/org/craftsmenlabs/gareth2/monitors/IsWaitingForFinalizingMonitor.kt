package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.providers.state.FinalizingExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class IsWaitingForFinalizingMonitor @Autowired constructor(
        finalizingExperimentProvider: FinalizingExperimentProvider,
        experimentStorage: ExperimentStorage) {

    init {
        finalizingExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { it.timing.waitingFinalizing = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
