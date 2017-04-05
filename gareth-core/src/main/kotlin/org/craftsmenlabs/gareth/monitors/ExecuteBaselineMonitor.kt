package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.ExperimentLifecycle
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.services.ExperimentService
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import java.util.concurrent.TimeUnit

@Service
class ExecuteBaselineMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentService: ExperimentService,
        private val experimentExecutor: ExperimentExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentService, ExperimentLifecycle.NEW) {
    private val delayedExperiments = mutableListOf<String>()
    override fun extend(observable: Observable<ExperimentDTO>): Observable<ExperimentDTO> {
        return observable
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val delayInSeconds = dateTimeService.getSecondsUntil(it.due!!)
                    log.info("Waiting $delayInSeconds seconds until executing baseline of experiment ${it.id}")
                    delayedExperiments.add(it.id)
                    val delay = Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                    return@delay delay
                }
                .map {
                    experimentExecutor.executeBaseline(it)
                }
    }
}
