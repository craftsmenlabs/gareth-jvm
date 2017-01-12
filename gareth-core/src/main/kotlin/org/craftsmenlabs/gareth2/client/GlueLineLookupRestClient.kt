package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth2.GlueLineLookup
import org.craftsmenlabs.gareth2.model.Experiment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class GlueLineLookupRestClient : GlueLineLookup {

    val log: Logger = LoggerFactory.getLogger(GlueLineLookupRestClient::class.java)

    @Autowired
    private lateinit var client: ExecutionRestClient

    override fun isExperimentReady(experiment: Experiment): Boolean {
        val details = experiment.details
        val lines = mapOf<String, String>(
                Pair("assume", details.assumption),
                Pair("baseline", details.baseline),
                Pair("failure", details.failure),
                Pair("success", details.success),
                Pair("time", details.time))
        return lines.all { client.isValidGlueLine(it.key, it.value) }
    }
}