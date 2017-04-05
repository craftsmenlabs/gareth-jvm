package org.craftsmenlabs.gareth.client

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.time.DurationExpressionParser
import org.junit.Test
import java.time.Duration

class GluelineValidatorRestClientTest {

    @Injectable
    private lateinit var experiment: ExperimentDTO
    @Injectable
    private lateinit var gluelines: Gluelines
    @Injectable
    private lateinit var client: ExecutionRestClient
    @Tested
    private lateinit var glueLineClient: GluelineValidatorRestClient

    @Injectable
    private lateinit var durationExpressionParser: DurationExpressionParser

    @Test
    fun testexperimentReady() {
        setupExperimentDetails()
        object : Expectations() {
            init {
                client.isValidGlueLine(GlueLineType.ASSUME, "A")
                result = true
                client.isValidGlueLine(GlueLineType.BASELINE, "B")
                result = true
                client.isValidGlueLine(GlueLineType.SUCCESS, "S")
                result = true
                client.isValidGlueLine(GlueLineType.FAILURE, "F")
                result = true
                durationExpressionParser.parse("T")
                result = Duration.ZERO
            }
        }
        assertThat(glueLineClient.validateGluelines(experiment.glueLines)).isTrue()
    }


    @Test
    fun testexperimentNotReady() {
        setupExperimentDetails()
        object : Expectations() {
            init {
                client.isValidGlueLine(GlueLineType.ASSUME, "A")
                result = false
            }
        }
        assertThat(glueLineClient.validateGluelines(experiment.glueLines)).isFalse()
    }

    fun setupExperimentDetails() {
        object : Expectations() {
            init {
                experiment.glueLines
                result = gluelines
                gluelines.assume
                result = "A"
                gluelines.baseline
                result = "B"
                gluelines.success
                result = "S"
                gluelines.failure
                result = "F"
                gluelines.time
                result = "T"
            }
        }
    }
}
