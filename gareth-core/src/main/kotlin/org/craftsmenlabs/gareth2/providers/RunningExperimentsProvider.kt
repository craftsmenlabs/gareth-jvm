package org.craftsmenlabs.gareth2

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RunningExperimentsProvider @Autowired constructor(private val storageProvider: StorageProvider) {

    val observable = storageProvider.observable
            .filter { it.experimentRun?.assumptionPlanned?.isAfter(LocalDateTime.now()) ?: false }
}