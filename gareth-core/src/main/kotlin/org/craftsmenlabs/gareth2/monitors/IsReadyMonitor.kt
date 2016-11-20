package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GluelineLookup
import org.craftsmenlabs.gareth2.providers.state.NewExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.schedulers.Schedulers
import java.time.LocalDateTime

@Service
class IsReadyMonitor @Autowired constructor(
        newExperimentProvider: NewExperimentProvider,
        gluelineLookup: GluelineLookup,
        experimentStorage: ExperimentStorage){

    init {
        newExperimentProvider.observable
                .subscribeOn(Schedulers.io())
                .filter { gluelineLookup.isExperimentReady(it) }
                .map { it.apply { it.timing.ready = LocalDateTime.now() } }
                .observeOn(Schedulers.computation())
                .subscribe { experimentStorage.save(it) }
    }
}
