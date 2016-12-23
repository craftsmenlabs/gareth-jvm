package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import rx.Observable
import rx.schedulers.Schedulers
import javax.annotation.PostConstruct

abstract class BaseMonitor constructor(
        protected val experimentProvider: ExperimentProvider,
        protected val dateTimeService: DateTimeService,
        protected val experimentStorage: ExperimentStorage,
        private val experimentState: ExperimentState) {

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { it.getState() == experimentState }
                .run { extend(this) }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }

    protected abstract fun extend(observable: Observable<Experiment>): Observable<Experiment>
}
