package org.craftsmenlabs.gareth.client

import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.time.DurationExpressionParser
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

    override fun gluelineIsValid(type: GlueLineType, glueline: String): Boolean {
        if (type == GlueLineType.TIME)
            return durationExpressionParser.parse(glueline) != null || client.isValidTimeGlueLine(glueline)
        else return client.isValidGlueLine(type, glueline)
    }

    override fun gluelinesAreValid(glueLines: Gluelines): Boolean {
        val lines = mapOf<GlueLineType, String>(
                Pair(GlueLineType.ASSUME, glueLines.assume),
                Pair(GlueLineType.BASELINE, glueLines.baseline),
                Pair(GlueLineType.FAILURE, glueLines.failure),
                Pair(GlueLineType.SUCCESS, glueLines.success))
        return gluelineIsValid(GlueLineType.TIME, glueLines.time) && lines.all { gluelineIsValid(it.key, it.value) }
    }
}