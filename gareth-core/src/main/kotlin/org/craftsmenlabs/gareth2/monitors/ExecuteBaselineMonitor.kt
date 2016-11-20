package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.state.WaitingForBaselineExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class ExecuteBaselineMonitor @Autowired constructor(
        waitingForBaselineExperimentProvider: WaitingForBaselineExperimentProvider,
        glueLineExecutor: GlueLineExecutor,
        experimentStorage: ExperimentStorage) {

    init {
        waitingForBaselineExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { glueLineExecutor.executeBaseline(it) } }
                .map { it.apply { it.timing.baselineExecuted = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
