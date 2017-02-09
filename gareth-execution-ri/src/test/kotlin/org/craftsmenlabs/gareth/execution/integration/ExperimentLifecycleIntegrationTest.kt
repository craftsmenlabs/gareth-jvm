package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.execution.dto.ExperimentRunEnvironmentBuilder
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
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

    val path = "http://localhost:8101/gareth/v1/"
    val restClient = BasicAuthenticationRestClient("user", "secret")

    @Test
    fun testBaseline() {
        val request = createRequest("sale of fruit", ExperimentRunEnvironmentBuilder.createEmpty())
        assertThat(doPut("${path}baseline", request).status).isEqualTo(ExecutionStatus.RUNNING)
    }

    @Test
    fun testSuccessfulAssume() {
        val request = createRequest("sale of fruit has risen by 81 per cent", ExperimentRunEnvironmentBuilder.createEmpty())
        assertThat(doPut("${path}assume", request).status).isEqualTo(ExecutionStatus.SUCCESS)
    }

    @Test
    fun testFailedAssume() {
        val request = createRequest("sale of fruit has risen by 79 per cent", ExperimentRunEnvironmentBuilder.createEmpty())
        assertThat(doPut("${path}assume", request).status).isEqualTo(ExecutionStatus.FAILURE)
    }

    @Test
    fun testDuration() {
        val request = createRequest("next Easter", ExperimentRunEnvironmentBuilder.createEmpty())
        val response = restClient.putAsEntity(request, Duration::class.java, "${path}time")
        assertThat(response.body.amount).isEqualTo(14400L)
    }

    @Test
    fun testSuccess() {
        val request = createRequest("send email to John", ExperimentRunEnvironmentBuilder.createEmpty())
        val environment = doPut("${path}success", request).environment
        assertThat(environment.items.find { it.key == "result" }?.value).isEqualTo("sending success mail to John")
    }

    @Test
    fun testFailure() {
        val request = createRequest("send email to Bob", ExperimentRunEnvironmentBuilder.createEmpty())
        assertThat(doPut("${path}failure", request).status).isEqualTo(ExecutionStatus.FAILURE)
    }


    fun createRequest(glueLine: String, environment: ExperimentRunEnvironment): ExecutionRequest {
        return ExecutionRequest(environment, glueLine)
    }

    fun doPut(path: String, dto: ExecutionRequest): ExecutionResult {
        val response = restClient.putAsEntity(dto, ExecutionResult::class.java, path)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        println(response.body)
        return response.body
    }

}

