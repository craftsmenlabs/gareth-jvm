package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.state.WaitingForFaulireExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class ExecuteFailureMonitor @Autowired constructor(
        waitingForFaulireExperimentProvider: WaitingForFaulireExperimentProvider,
        glueLineExecutor: GlueLineExecutor,
        experimentStorage: ExperimentStorage) {

    init {
        waitingForFaulireExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { glueLineExecutor.executeFailure(it) } }
                .map { it.apply { it.timing.finalizingExecuted = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
