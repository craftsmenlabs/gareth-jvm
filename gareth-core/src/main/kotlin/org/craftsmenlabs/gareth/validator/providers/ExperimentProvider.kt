package org.craftsmenlabs.gareth.validator.providers

import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.services.ExperimentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

@Service
class ExperimentProvider @Autowired constructor(
        allExperimentListProvider: AllExperimentListProvider,
        experimentService: ExperimentService) {

    private val publishSubject = PublishSubject.create<ExperimentDTO>()

    init {
        allExperimentListProvider.observable
                .debounce(10, TimeUnit.MINUTES)
                .flatMap { it.toObservable() }
                .subscribe {
                    publishSubject.onNext(it)
                }

        experimentService.setListener {
            publishSubject.onNext(it)
        }
    }

    // TODO: Somehow we should not emit the same event twice within the time it take to process a call to the executor.
    // Waiting for assume has counter measures, but all other monitors do not.

    val observable = publishSubject
            .subscribeOn(Schedulers.computation())
}
