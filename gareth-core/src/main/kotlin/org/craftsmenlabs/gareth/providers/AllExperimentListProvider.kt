package org.craftsmenlabs.gareth.providers

import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.services.ExperimentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject

@Service
class AllExperimentListProvider @Autowired constructor(private val experimentService: ExperimentService) {

    private val behaviourSubject = BehaviorSubject.create<List<ExperimentDTO>>()

    init {
        behaviourSubject.doOnSubscribe {
            publishAllExperiments()
        }
    }

    fun publishAllExperiments() {
        val allExperiments = experimentService.loadAllExperiments()
        behaviourSubject.onNext(allExperiments)
    }

    val observable = behaviourSubject
            .subscribeOn(Schedulers.computation())
}
