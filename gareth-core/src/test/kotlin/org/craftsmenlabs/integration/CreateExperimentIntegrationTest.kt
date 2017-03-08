package org.craftsmenlabs.integration

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.Application
import org.craftsmenlabs.gareth.integration.TestConfig
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
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
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class, TestConfig::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = arrayOf("test", "NOAUTH"))
class CreateExperimentIntegrationTest {

    val basePath = "http://localhost:8100/gareth/v1"
    val experimentsPath = "$basePath/experiments"
    val templatesPath = "$basePath/templates"
    val lookupPath = "$basePath/glueline"
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
    fun a_testLookupGlueLines() {
        fun getGlueLine(glueLine: GlueLineType, content: String) {
            val entity = template.getForEntity("$lookupPath?type=$glueLine&content=$content", GlueLineSearchResultDTO::class.java)
            if (!entity.statusCode.is2xxSuccessful) {
                Assertions.fail("call not OK")
            }
            val dto = entity.body
            assertThat(dto.exact).isEqualTo(content)
            assertThat(dto.suggestions).containsExactly(content)
        }
        getGlueLine(GlueLineType.BASELINE, "get sale of fruit")
        getGlueLine(GlueLineType.ASSUME, "has risen")
        getGlueLine(GlueLineType.SUCCESS, "send mail to Moos")
        getGlueLine(GlueLineType.FAILURE, "send mail to Sam")
        getGlueLine(GlueLineType.TIME, "3 weeks")

    }

    @Test
    fun b_createExperimentWithoutStartDate() {
        val createTemplateDTO = createTemplate()
        val template = postTemplate(createTemplateDTO)
        val experimentCreateDTO = ExperimentCreateDTO(templateId = template.id)
        val created = postExperiment(experimentsPath, experimentCreateDTO)
        Thread.sleep(2000)
        assertThat(created.id).isPositive()
        val saved = storage.getById(created.id)
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.ready).isNotNull()
        /*assertThat(searchExperiment(completed = false)).hasSize(1)
        assertThat(searchExperiment(date = today, completed = false)).hasSize(1)
        assertThat(searchExperiment(completed = true)).isEmpty()
        assertThat(searchExperiment(date = tomorrow)).isEmpty()
        assertThat(searchExperiment()).hasSize(1)*/
    }

    @Test
    fun c_createExperimentWithFaultyBaseline() {
        val orig = createTemplate()
        val dto = orig.copy(glueLines = orig.glueLines.copy(baseline = "sale of computers"))
        val created = postTemplate(dto)
        val saved = storage.getTemplateById(created.id)
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.ready).isNull()
        val updated = updateTemplate(ExperimentTemplateUpdateDTO(id = saved.id, assume = "sale of fruit"))
        assertThat(updated.ready).isNotNull()

    }

    @Test
    fun d_createAndStartExperiment() {
        val template = postTemplate(createTemplate())
        val dto = ExperimentCreateDTO(templateId = template.id, startDate = LocalDateTime.now())
        val created = postExperiment(experimentsPath, dto)
        Thread.sleep(1000)

        var saved = storage.getById(created.id)
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.ready).isNotNull()
        assertThat(saved.timing.started).isNotNull()
        assertThat(saved.results.status).isEqualTo(ExecutionStatus.PENDING)

        Thread.sleep(3000)
        saved = storage.getById(created.id)
        assertThat(saved.results.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.timing.assumeExecuted).isNotNull()
        assertThat(saved.timing.waitingFinalizing).isNotNull()
        assertThat(saved.timing.completed).isNotNull()

    }

    private fun createTemplate(): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = "Hello world",
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "2 seconds",
                        success = "send email to Sam",
                        failure = "send email to Moos"
                ),
                value = 42)
    }

    private fun postTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        val builder = RequestEntity.post(URI(templatesPath)).contentType(MediaType.APPLICATION_JSON).body(dto)
        val response = template.exchange(builder, ExperimentTemplateDTO::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

    private fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        val builder = RequestEntity.put(URI(templatesPath)).contentType(MediaType.APPLICATION_JSON).body(dto)
        val response = template.exchange(builder, ExperimentTemplateDTO::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

    private fun postExperiment(path: String, dto: ExperimentCreateDTO): ExperimentDTO {
        val builder = RequestEntity.post(URI(path)).contentType(MediaType.APPLICATION_JSON).body(dto)
        val response = template.exchange(builder, ExperimentDTO::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

    private fun searchExperiment(date: String? = null, completed: Boolean? = null): List<ExperimentDTO> {
        val completedQuery = if (completed != null) "completed=$completed" else "";
        val createdQuery = if (date != null) "created=$date" else ""
        val url = "$experimentsPath?$createdQuery&$completedQuery"
        val builder = RequestEntity.get(URI(url)).build()
        val response = template.exchange(builder, Array<ExperimentDTO>::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body.toList()
    }
}