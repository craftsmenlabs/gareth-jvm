package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.state.WaitingForAssumeExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class ExecuteAssumeMonitor @Autowired constructor(
        waitingForAssumeExperimentProvider: WaitingForAssumeExperimentProvider,
        glueLineExecutor: GlueLineExecutor,
        experimentStorage: ExperimentStorage) {

    init {
        waitingForAssumeExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.apply { it.results.success = glueLineExecutor.executeAssumption(it) } }
                .map { it.apply { it.timing.assmueExected = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
