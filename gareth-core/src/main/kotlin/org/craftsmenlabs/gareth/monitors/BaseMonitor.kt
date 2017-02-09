package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.schedulers.Schedulers
import javax.annotation.PostConstruct

abstract class BaseMonitor constructor(
        protected val experimentProvider: ExperimentProvider,
        protected val dateTimeService: TimeService,
        protected val experimentStorage: ExperimentStorage,
        private val experimentState: ExperimentState) {

    val log: Logger = LoggerFactory.getLogger(BaseMonitor::class.java)

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.computation())
                .map { Experiment(it.details.copy(), it.timing.copy(), it.results.copy(), it.id, it.environment) }
                .filter { it.getState() == experimentState }
                .run { extend(this) }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }

    protected abstract fun extend(observable: Observable<Experiment>): Observable<Experiment>
}
