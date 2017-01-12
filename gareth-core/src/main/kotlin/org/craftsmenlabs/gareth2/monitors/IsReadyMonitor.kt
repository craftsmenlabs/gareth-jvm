package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GlueLineLookup
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class IsReadyMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: DateTimeService,
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
