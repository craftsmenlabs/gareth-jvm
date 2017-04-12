package org.craftsmenlabs.gareth.client

import mockit.Deencapsulation
import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.craftsmenlabs.gareth.validator.client.rest.GluelineValidatorRestClient
import org.craftsmenlabs.gareth.validator.model.DefinitionInfo
import org.craftsmenlabs.gareth.validator.model.Gluelines
import org.craftsmenlabs.gareth.validator.rest.BasicAuthenticationRestClient
import org.craftsmenlabs.gareth.validator.time.DurationExpressionParser
import org.junit.Test
import org.springframework.http.ResponseEntity
import java.time.Duration

class GluelineValidatorRestClientTest {

    @Injectable
    private lateinit var gluelines: Gluelines
    @Injectable
    lateinit var restClient: BasicAuthenticationRestClient
    @Injectable
    lateinit var definitionInfo: DefinitionInfo
    @Tested
    private lateinit var glueLineClient: GluelineValidatorRestClient

    @Injectable
    private lateinit var serviceDiscovery: ExecutionServiceDiscovery

    @Injectable
    private lateinit var durationExpressionParser: DurationExpressionParser

    @Test
    fun testexperimentReady() {
        setupExperimentDetails()
        object : Expectations() {
            init {
                durationExpressionParser.parse("T")
                result = Duration.ZERO
            }
        }
        assertThat(glueLineClient.validateGluelines("acme", gluelines)).isTrue()
    }


    @Test
    fun testexperimentReadyWithEmptySuccessAndFailure() {
        setupExperimentDetails(true)
        object : Expectations() {
            init {

                durationExpressionParser.parse("T")
                result = Duration.ZERO
            }
        }
        assertThat(glueLineClient.validateGluelines("acme", gluelines)).isTrue()
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

                serviceDiscovery.createUrl("acme", anyString)
                result = "http://localhost"

                restClient.getAsEntity(DefinitionInfo::class.java, "http://localhost")
                result = ResponseEntity.ok(definitionInfo)

                definitionInfo.glueline
                result = "glueline"
            }
        }
        Deencapsulation.setField(glueLineClient, "restClient", restClient)
    }

}
