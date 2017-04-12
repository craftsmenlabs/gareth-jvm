package org.craftsmenlabs.gareth.client

import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.time.DurationExpressionParser
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!mock")
class GluelineValidatorRestClient : GluelineValidator {

    val log: Logger = LoggerFactory.getLogger(GluelineValidatorRestClient::class.java)

    @Autowired
    private lateinit var client: ExecutionRestClient

    @Autowired
    private lateinit var durationExpressionParser: DurationExpressionParser

    override fun gluelineIsValid(type: GlueLineType, glueline: String?): Boolean {
        if (glueline == null || glueline.isBlank()) {
            //only success and failure are allowed to be blank
            return type == GlueLineType.SUCCESS || type == GlueLineType.FAILURE
        }
        if (type == GlueLineType.TIME)
            return durationExpressionParser.parse(glueline) != null || client.isValidTimeGlueLine(glueline)
        else return client.isValidGlueLine(type, glueline)
    }

    override fun validateGluelines(gluelines: Gluelines): Boolean {
        val lines = mapOf<GlueLineType, String?>(
                Pair(GlueLineType.ASSUME, gluelines.assume),
                Pair(GlueLineType.BASELINE, gluelines.baseline),
                Pair(GlueLineType.TIME, gluelines.time),
                Pair(GlueLineType.FAILURE, gluelines.failure),
                Pair(GlueLineType.SUCCESS, gluelines.success))
        return lines.all { gluelineIsValid(it.key, it.value) }
    }
}