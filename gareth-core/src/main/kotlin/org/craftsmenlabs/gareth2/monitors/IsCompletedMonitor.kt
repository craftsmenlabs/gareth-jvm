package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class IsCompletedMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: DateTimeService,
        experimentStorage: ExperimentStorage)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.FINALISATION_EXECUTED) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable.map { it.apply { it.timing.completed = dateTimeService.now() } }
    }
}
