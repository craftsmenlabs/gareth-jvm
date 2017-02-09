package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.GlueLineLookup
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class IsReadyMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage,
        private val glueLineLookup: GlueLineLookup)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.NEW) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { glueLineLookup.isExperimentReady(it) }
                .map { it.copy(timing = it.timing.copy(ready = dateTimeService.now())) }
    }
}
