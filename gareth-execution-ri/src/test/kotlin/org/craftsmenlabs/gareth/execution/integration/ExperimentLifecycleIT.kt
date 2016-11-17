package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.Application
import org.craftsmenlabs.gareth.execution.dto.*
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import java.net.URI

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("Test")
class ExperimentLifecycleIT {

    val path = "http://localhost:8090/gareth/v1/"
    val template = RestTemplate()

    @Test
    fun testBaseline() {
        val request = createRequest("sale of fruit", ExperimentRunEnvironmentDTO.createEmpty())
        assertThat(doPut("${path}baseline", request).status).isEqualTo(ExecutionStatus.RUNNING)
    }

    @Test
    fun testSuccessfulAssume() {
        val request = createRequest("sale of fruit has risen by 81 per cent", ExperimentRunEnvironmentDTO.createEmpty())
        assertThat(doPut("${path}assume", request).status).isEqualTo(ExecutionStatus.SUCCESS)
    }

    @Test
    fun testFailedAssume() {
        val request = createRequest("sale of fruit has risen by 79 per cent", ExperimentRunEnvironmentDTO.createEmpty())
        assertThat(doPut("${path}assume", request).status).isEqualTo(ExecutionStatus.FAILURE)
    }

    @Test
    fun testDuration() {
        val request = RequestEntity(createRequest("next Easter", ExperimentRunEnvironmentDTO.createEmpty()), HttpMethod.PUT, URI("${path}time"))
        val response = template.exchange(request, DurationDTO::class.java)
        assertThat(response.body.amount).isEqualTo(14400L)
    }

    @Test
    fun testSuccess() {
        val request = createRequest("send email to John", ExperimentRunEnvironmentDTO.createEmpty())
        assertThat(doPut("${path}success", request).environment.getValueByKey("emailtext")).isEqualTo("sending mail to John")
    }

    @Test
    fun testFailure() {
        val request = createRequest("send email to Bob", ExperimentRunEnvironmentDTO.createEmpty())
        assertThat(doPut("${path}failure", request).status).isEqualTo(ExecutionStatus.FAILURE)
    }


    fun createRequest(glueLine: String, environment: ExperimentRunEnvironmentDTO): ExecutionRequestDTO {
        return ExecutionRequestDTO(environment, glueLine)
    }

    fun doPut(path: String, dto: ExecutionRequestDTO): ExecutionResultDTO {
        val builder = RequestEntity.put(URI(path)).contentType(MediaType.APPLICATION_JSON).body(dto)
        val response = template.exchange(builder, ExecutionResultDTO::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        println(response.body)
        return response.body
    }

}

