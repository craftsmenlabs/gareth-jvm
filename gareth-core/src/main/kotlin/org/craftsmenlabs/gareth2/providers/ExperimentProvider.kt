package org.craftsmenlabs.gareth2.providers

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

@Service
class ExperimentProvider @Autowired constructor(
        allExperimentListProvider: AllExperimentListProvider,
        experimentStorage: ExperimentStorage) {

    private val publishSubject = PublishSubject.create<Experiment>()

    init {
        allExperimentListProvider.observable
                .debounce(10, TimeUnit.MINUTES)
                .flatMap { it.toObservable() }
                .subscribe {
                    publishSubject.onNext(it)
                }

        experimentStorage.setListener {
            publishSubject.onNext(it)
        }
    }

    // TODO: Some how we should not emit the same event twice whitin the time it take to process an call to the executor.
    // Waiting for assume has counter measures, but all other monitors do not.

    val observable = publishSubject
            .subscribeOn(Schedulers.computation())
}
