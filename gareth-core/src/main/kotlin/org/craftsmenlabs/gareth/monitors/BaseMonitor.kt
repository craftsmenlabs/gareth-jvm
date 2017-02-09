package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import rx.Observable
import rx.schedulers.Schedulers
import javax.annotation.PostConstruct

abstract class BaseMonitor constructor(
        protected val experimentProvider: ExperimentProvider,
        protected val dateTimeService: TimeService,
        protected val experimentStorage: ExperimentStorage,
        private val experimentState: ExperimentState) {

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.copy() }
                .filter { it.getState() == experimentState }
                .run { extend(this) }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }

    protected abstract fun extend(observable: Observable<Experiment>): Observable<Experiment>
}
