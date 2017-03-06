package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class ExecuteFailureMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage,
        private val glueLineExecutor: GlueLineExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.WAITING_FOR_FINALISATION) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { it.results.status == ExecutionStatus.FAILURE }
                .map { it.copy(environment = glueLineExecutor.executeFailure(it).environment) }
                .map { it.copy(timing = it.timing.copy(finalizingExecuted = dateTimeService.now())) }
    }
}
