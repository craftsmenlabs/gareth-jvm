package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class ExecuteAssumeMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: DateTimeService,
        experimentStorage: ExperimentStorage,
        private val glueLineExecutor: GlueLineExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.WAITING_FOR_ASSUME) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .map {
                    val result = glueLineExecutor.executeAssume(it);
                    it.copy(results = it.results.copy(status = result.status), environment = result.environment)
                }
                .map { it.copy(timing = it.timing.copy(assumeExecuted = dateTimeService.now())) }
    }
}
