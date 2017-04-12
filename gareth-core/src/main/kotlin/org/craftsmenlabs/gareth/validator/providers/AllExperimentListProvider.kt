package org.craftsmenlabs.gareth.validator.providers

import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.services.ExperimentService
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
