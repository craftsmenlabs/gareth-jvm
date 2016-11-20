package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.state.WaitingForSuccessExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class ExecuteSuccessMonitor @Autowired constructor(
        waitingForSuccessExperimentProvider: WaitingForSuccessExperimentProvider,
        glueLineExecutor: GlueLineExecutor,
        experimentStorage: ExperimentStorage) {

    init {
        waitingForSuccessExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { glueLineExecutor.executeSuccess(it) } }
                .map { it.apply { it.timing.finalizingExecuted = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
