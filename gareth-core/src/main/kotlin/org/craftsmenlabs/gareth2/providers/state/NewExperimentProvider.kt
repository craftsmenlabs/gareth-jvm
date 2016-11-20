package org.craftsmenlabs.gareth2.providers.state

import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NewExperimentProvider @Autowired constructor(experimentProvider: ExperimentProvider) {

    val observable = experimentProvider.observable
            .filter { it.timing.ready == null }
}
