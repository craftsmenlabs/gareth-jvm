package org.craftsmenlabs.gareth2.providers

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.rx.KSchedulers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.subjects.BehaviorSubject

@Service
class ExperimentProvider @Autowired constructor(private val experimentStorage: ExperimentStorage) {

    private val behaviourSubject = BehaviorSubject.create<Experiment>()

    init {
        behaviourSubject.doOnSubscribe {
            publishAllExperiments()
        }

        experimentStorage.setListener {
            behaviourSubject.onNext(it)
        }
    }

    fun publishAllExperiments() {
        val allExperiments = experimentStorage.loadAllExperiments()
        allExperiments.forEach { behaviourSubject.onNext(it) }
    }

    val observable = behaviourSubject
            .subscribeOn(KSchedulers.computation())
}
