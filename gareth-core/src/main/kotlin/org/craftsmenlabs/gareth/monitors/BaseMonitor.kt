package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.ExperimentLifecycle
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.services.ExperimentService
import org.craftsmenlabs.gareth.time.TimeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import javax.annotation.PostConstruct

abstract class BaseMonitor constructor(
        protected val experimentProvider: ExperimentProvider,
        protected val dateTimeService: TimeService,
        protected val experimentService: ExperimentService,
        private val experimentState: ExperimentLifecycle) {

    val log: Logger = LoggerFactory.getLogger(BaseMonitor::class.java)


    private val INVALID_ID = "INVALID"

    @PostConstruct
    fun start() {
        experimentProvider.observable
                .subscribeOn(Schedulers.io())
                .map { it.copy() }
                .filter { it.getLifecycleStage() == experimentState }
                .flatMap {
                    var res: Observable<ExperimentDTO>
                    try {
                        res = extend(it.toSingletonObservable())
                    } catch (t: Throwable) {
                        log.error("extend failed", t)
                        res = ExperimentDTO.createDefault(dateTimeService.now()).copy(id = INVALID_ID).toSingletonObservable()
                    }
                    return@flatMap res
                }
                .filter { it.id != INVALID_ID }
                .observeOn(Schedulers.computation())
                .subscribe {
                    experimentService.updateExperiment(it)
                }
    }

    protected abstract fun extend(observable: Observable<ExperimentDTO>): Observable<ExperimentDTO>
}
