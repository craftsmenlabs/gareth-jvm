package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import javax.annotation.PostConstruct

@Service
class ExecuteAssumeMonitor @Autowired constructor(
        private val experimentProvider: ExperimentProvider,
        private val dateTimeService: DateTimeService,
        private val glueLineExecutor: GlueLineExecutor,
        private val experimentStorage: ExperimentStorage) {

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { it.getState() == ExperimentState.WAITING_FOR_ASSUME }
                .map { it.apply { it.results.success = glueLineExecutor.executeAssume(it) } }
                .map { it.apply { it.timing.assumeExecuted = dateTimeService.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
