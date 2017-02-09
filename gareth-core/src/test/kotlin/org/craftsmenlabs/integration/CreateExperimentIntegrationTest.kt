package org.craftsmenlabs.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.Application
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.integration.TestConfig
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import java.net.URI

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class, TestConfig::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = arrayOf("test"))
class CreateExperimentIntegrationTest {

    val path = "http://localhost:8100/gareth/v1/experiments"
    val template = RestTemplate()

    @Autowired
    lateinit var storage: ExperimentStorage

    @Autowired
    lateinit var timeService: TimeService

    lateinit var today: String
    lateinit var tomorrow: String

    @Before
    fun setup() {
        today = DateFormatUtils.formatToDateString(timeService.midnight())
        tomorrow = DateFormatUtils.formatToDateString(timeService.midnight().plusDays(1))
    }

    @Test
    fun a_createExperiment() {
        val dto = createDTO()
        val created = doPut(path, dto)
        Thread.sleep(2000)
        assertThat(created.id).isPositive()
        val saved = storage.getById(created.id)
        assertThat(saved.details.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.ready).isNotNull()
        assertThat(saved.environment.items[0].key).isEqualTo("fruit")
        assertThat(saved.environment.items[0].value).isEqualTo("apples")
        /* assertThat(searchExperiment(date = today, completed = false)).hasSize(1)
         assertThat(searchExperiment(date = today, completed = true)).isEmpty()
         assertThat(searchExperiment(date = tomorrow)).isEmpty()
         assertThat(searchExperiment()).hasSize(1)*/
    }

    @Test
    fun b_createExperimentWithFaultyBaseline() {
        val dto = createDTO().copy(baseline = "sale of computers")
        val created = doPut(path, dto)
        val saved = storage.getById(created.id)
        assertThat(saved.details.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.ready).isNull()
        /*  assertThat(searchExperiment(date = today, completed = false)).hasSize(1)*/
    }

    @Test
    fun c_createAndStartExperiment() {
        val dto = createDTO()
        val created = doPut(path, dto)
        val builder = RequestEntity.put(URI("$path/${created.id}/start")).contentType(MediaType.APPLICATION_JSON)
        template.exchange(builder.build(), ExperimentDTO::class.java)
        Thread.sleep(1000)

        var saved = storage.getById(created.id)
        assertThat(saved.details.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.ready).isNotNull()
        assertThat(saved.timing.started).isNotNull()
        assertThat(saved.results.status).isEqualTo(ExecutionStatus.RUNNING)

        Thread.sleep(3000)
        saved = storage.getById(created.id)
        assertThat(saved.results.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.timing.assumeExecuted).isNotNull()
        assertThat(saved.timing.waitingFinalizing).isNotNull()
        assertThat(saved.timing.completed).isNotNull()
    }

    private fun createDTO(): ExperimentCreateDTO {
        return ExperimentCreateDTO(name = "Hello world",
                baseline = "sale of fruit",
                assume = "sale of fruit has risen by 81 per cent",
                time = "2 seconds",
                success = "send email to Sam",
                failure = "send email to Moos",
                weight = 42,
                environment = ExperimentRunEnvironment(listOf(EnvironmentItem("fruit", "apples", ItemType.STRING))))
    }

    fun doPut(path: String, dto: ExperimentCreateDTO): ExperimentDTO {
        val builder = RequestEntity.put(URI(path)).contentType(MediaType.APPLICATION_JSON).body(dto)
        val response = template.exchange(builder, ExperimentDTO::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

    fun searchExperiment(date: String? = null, completed: Boolean? = null): List<ExperimentDTO> {
        val completedQuery = if (completed != null) "completed=$completed" else "";
        val createdQuery = if (date != null) "created=$date" else ""
        val url = "$path?$createdQuery&$completedQuery"
        val builder = RequestEntity.get(URI(url)).build()
        val response = template.exchange(builder, Array<ExperimentDTO>::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body.toList()
    }
}