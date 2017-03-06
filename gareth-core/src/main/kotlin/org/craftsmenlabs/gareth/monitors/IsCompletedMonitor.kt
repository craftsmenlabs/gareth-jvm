package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class IsCompletedMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.FINALISATION_EXECUTED) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .map { it.copy(timing = it.timing.copy(completed = dateTimeService.now())) }
    }
}
