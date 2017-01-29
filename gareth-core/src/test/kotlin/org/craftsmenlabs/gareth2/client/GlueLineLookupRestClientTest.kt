package org.craftsmenlabs.gareth2.client

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails
import org.craftsmenlabs.gareth2.time.DurationExpressionParser
import org.junit.Test
import java.time.Duration

class GlueLineLookupRestClientTest {
    @Injectable
    private lateinit var experiment: Experiment
    @Injectable
    private lateinit var details: ExperimentDetails
    @Injectable
    private lateinit var client: ExecutionRestClient
    @Tested
    private lateinit var glueLineClient: GlueLineLookupRestClient

    @Injectable
    private lateinit var durationExpressionParser: DurationExpressionParser

    @Test
    fun testexperimentReady() {
        setupExperimentDetails()
        object : Expectations() {
            init {
                client.isValidGlueLine("assume", "A")
                result = true
                client.isValidGlueLine("baseline", "B")
                result = true
                client.isValidGlueLine("success", "S")
                result = true
                client.isValidGlueLine("failure", "F")
                result = true
                durationExpressionParser.parse("T")
                result = Duration.ZERO
            }
        }
        assertThat(glueLineClient.isExperimentReady(experiment)).isTrue()
    }


    @Test
    fun testexperimentNotReady() {
        setupExperimentDetails()
        object : Expectations() {
            init {
                client.isValidGlueLine("assume", "A")
                result = false
            }
        }
        assertThat(glueLineClient.isExperimentReady(experiment)).isFalse()
    }

    fun setupExperimentDetails() {
        object : Expectations() {
            init {
                experiment.details
                result = details
                details.assume
                result = "A"
                details.baseline
                result = "B"
                details.success
                result = "S"
                details.failure
                result = "F"
                details.time
                result = "T"
            }
        }
    }
}
