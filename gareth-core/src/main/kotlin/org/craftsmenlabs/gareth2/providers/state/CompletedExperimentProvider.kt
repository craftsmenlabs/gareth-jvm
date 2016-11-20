package org.craftsmenlabs.gareth2.providers.state

import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompletedExperimentProvider @Autowired constructor(experimentProvider: ExperimentProvider) {

    val observable = experimentProvider.observable
            .filter { it.timing.ready != null }
            .filter { it.timing.started != null }
            .filter { it.timing.waitingForBaseline != null }
            .filter { it.timing.baselineExecuted != null }
            .filter { it.timing.waitingForAssume != null }
            .filter { it.timing.assmueExected != null }
            .filter { it.timing.waitingFinalizing != null }
            .filter { it.results.success != null }
            .filter { it.timing.finalizingExecuted != null }

}
