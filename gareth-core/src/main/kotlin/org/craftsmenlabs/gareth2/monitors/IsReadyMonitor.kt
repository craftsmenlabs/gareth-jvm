package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GluelineLookup
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
        private val gluelineLookup: GluelineLookup)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.NEW) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { gluelineLookup.isExperimentReady(it) }
                .map { it.apply { it.timing.ready = dateTimeService.now() } }
    }
}
