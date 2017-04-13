package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.execution.dto.ExperimentRunEnvironmentBuilder
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.rest.BasicAuthenticationRestClient
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GarethExecutionApplication::class, SaleOfFruit::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(properties = arrayOf("server.port=8101"))
@ActiveProfiles("test")
class ExperimentLifecycleIntegrationTest {

    val path = "http://localhost:8101/gareth/validator/v1/"
    val restClient = BasicAuthenticationRestClient("user", "secret")

    private val fullGluelineSet = ValidatedGluelines(
            baseline = "sale of fruit",
            assume = "sale of fruit has risen by 81 per cent",
            time = "next Easter",
            success = "send email to John",
            failure = "send email to Bob")

    private val noFinalizationGluelineSet = ValidatedGluelines(
            baseline = "sale of fruit",
            assume = "sale of fruit has risen by 81 per cent",
            time = "next Easter")

    private val failingBaselineGluelineSet = ValidatedGluelines(
            baseline = "get snake oil",
            assume = "sale of fruit has risen by 81 per cent",
            time = "2 days")

    @Test
    fun testBaseline() {
        assertThat(doPut("${path}baseline", createRequest(fullGluelineSet)).status).isEqualTo(ExecutionStatus.RUNNING)
    }

    @Test
    fun testSuccessfulAssume() {
        assertThat(doPut("${path}assume", createRequest(fullGluelineSet)).status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(doPut("${path}assume", createRequest(noFinalizationGluelineSet)).status).isEqualTo(ExecutionStatus.SUCCESS)
    }

    @Test
    fun testFailedBaseline() {
        val request = createRequest(failingBaselineGluelineSet)
        assertThat(doPut("${path}baseline", request).status).isEqualTo(ExecutionStatus.ERROR)
    }

    @Test
    fun testFailedAssume() {
        val request = createRequest(fullGluelineSet)
        val failedAssume = request.copy(glueLines = request.glueLines.copy(assume = "sale of fruit has risen by 79 per cent"))
        assertThat(doPut("${path}assume", failedAssume).status).isEqualTo(ExecutionStatus.FAILURE)
    }

    @Test
    fun testDuration() {
        val response = restClient.putAsEntity(createRequest(fullGluelineSet), Duration::class.java, "${path}time")
        assertThat(response.body.amount).isEqualTo(14400L)
    }

    fun createRequest(gluelines: ValidatedGluelines): ExecutionRequest {
        return ExecutionRequest(ExperimentRunEnvironmentBuilder.createEmpty(), gluelines)
    }

    fun doPut(path: String, dto: ExecutionRequest): ExecutionResult {
        val response = restClient.putAsEntity(dto, ExecutionResult::class.java, path)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        println(response.body)
        return response.body
    }

}

