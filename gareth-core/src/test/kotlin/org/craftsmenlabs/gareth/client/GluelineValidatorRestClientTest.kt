package org.craftsmenlabs.gareth.client

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.client.ExecutionRestClient
import org.craftsmenlabs.gareth.validator.client.GluelineValidatorRestClient
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.craftsmenlabs.gareth.validator.model.Gluelines
import org.craftsmenlabs.gareth.validator.time.DurationExpressionParser
import org.junit.Test
import java.time.Duration

class GluelineValidatorRestClientTest {

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
        assertThat(glueLineClient.validateGluelines(gluelines)).isTrue()
    }


    @Test
    fun testexperimentReadyWithEmptySuccessAndFailure() {
        setupExperimentDetails(true)
        object : Expectations() {
            init {
                client.isValidGlueLine(GlueLineType.ASSUME, "A")
                result = true
                client.isValidGlueLine(GlueLineType.BASELINE, "B")
                result = true
                client.isValidGlueLine(GlueLineType.SUCCESS, "S")
                maxTimes = 0
                client.isValidGlueLine(GlueLineType.FAILURE, "F")
                maxTimes = 0
                durationExpressionParser.parse("T")
                result = Duration.ZERO
            }
        }
        assertThat(glueLineClient.validateGluelines(gluelines)).isTrue()
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
        assertThat(glueLineClient.validateGluelines(gluelines)).isFalse()
    }

    fun setupExperimentDetails(emptyFinalization: Boolean = false) {
        object : Expectations() {
            init {
                gluelines.assume
                result = "A"
                gluelines.baseline
                result = "B"
                gluelines.success
                result = if (emptyFinalization) null else "S"
                gluelines.failure
                result = if (emptyFinalization) null else "F"
                gluelines.time
                result = "T"
            }
        }
    }

}
