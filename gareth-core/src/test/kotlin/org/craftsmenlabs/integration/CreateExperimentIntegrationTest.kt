package org.craftsmenlabs.integration

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.Application
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.integration.TestConfig
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.ExperimentEndpointClient
import org.craftsmenlabs.gareth.rest.ExperimentTemplateEndpointClient
import org.craftsmenlabs.gareth.rest.GluelineLookupEndpointClient
import org.craftsmenlabs.gareth.rest.RestClientConfig
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import retrofit2.Response
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class, TestConfig::class, RestClientConfig::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = arrayOf("test", "NOAUTH"))
class CreateExperimentIntegrationTest {

    @Autowired
    lateinit var storage: ExperimentStorage

    @Autowired
    lateinit var templateClient: ExperimentTemplateEndpointClient

    @Autowired
    lateinit var experimentClient: ExperimentEndpointClient

    @Autowired
    lateinit var gluelineClient: GluelineLookupEndpointClient

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
            val response = gluelineClient.lookupGlueline(glueLine, content).execute()
            if (!response.isSuccessful) {
                Assertions.fail("call not OK")
            }
            val dto = response.body()
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
    fun b_createExperimentWithoutStartDate1() {
        val createTemplateDTO = createTemplate("Goodbye world")
        val template = storage.createTemplate(createTemplateDTO)
        val created = storage.createExperiment(template.id, LocalDateTime.now())
        Thread.sleep(2000)
        assertThat(created.id).isNotNull()
        val saved = storage.getById(created.id)
        assertThat(saved.name).isEqualTo("Goodbye world")
        assertThat(saved.timing.created).isNotNull()

    }


    @Test
    fun b_createExperimentWithoutStartDate() {
        val createTemplateDTO = createTemplate("Hello world")
        val template = postTemplate(createTemplateDTO)
        val experimentCreateDTO = ExperimentCreateDTO(templateId = template.id)
        val created = postExperiment(experimentCreateDTO)
        Thread.sleep(2000)
        assertThat(created.id).isNotNull()
        val saved = storage.getById(created.id)
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.timing.created).isNotNull()

    }

    @Test
    fun c_createExperimentWithFaultyBaseline() {
        val orig = createTemplate("Hello world2")
        val dto = orig.copy(glueLines = orig.glueLines.copy(baseline = "sale of computers"))
        val created = postTemplate(dto)
        val saved = storage.getTemplateById(created.id)
        assertThat(saved.name).isEqualTo("Hello world2")
        assertThat(saved.ready).isNull()
        val updated = updateTemplate(ExperimentTemplateUpdateDTO(id = saved.id, assume = "sale of fruit"))
        assertThat(updated.ready).isNotNull()

    }

    @Test
    fun d_createAndStartExperiment() {
        val template = postTemplate(createTemplate("Hello world3"))
        val dto = ExperimentCreateDTO(templateId = template.id, dueDate = LocalDateTime.now().plusSeconds(5))
        val created = postExperiment(dto)
        assertThat(storage.getById(created.id).status).isEqualTo(ExecutionStatus.PENDING)
        Thread.sleep(5000)

        var saved = storage.getById(created.id)
        assertThat(saved.name).isEqualTo("Hello world3")
        assertThat(saved.timing.created).isNotNull()
        assertThat(saved.timing.due).isNotNull()
        assertThat(saved.status).isEqualTo(ExecutionStatus.RUNNING)

        Thread.sleep(5000)
        saved = storage.getById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.timing.completed).isNotNull()

    }

    private fun createTemplate(name: String): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = name,
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "5 seconds",
                        success = "send email to Sam",
                        failure = "send email to Moos"
                ),
                value = 42)
    }


    private fun postTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        val response = templateClient.create(dto).execute()
        assertThat(response.isSuccessful).describedAs("Could not create template " + parseError(response)).isTrue()
        return response.body()
    }

    private fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        val response = templateClient.update(dto).execute()
        assertThat(response.isSuccessful).describedAs("Could not update template: " + parseError(response)).isTrue()
        return response.body()
    }

    private fun postExperiment(dto: ExperimentCreateDTO): ExperimentDTO {
        val response = experimentClient.start(dto).execute()
        return response.body()
    }

    private fun searchExperiment(date: String? = null, completed: Boolean? = null): List<ExperimentDTO> {
        val response = experimentClient.getFiltered(date, completed).execute()
        assertThat(response.isSuccessful).describedAs("Could not find experiments " + parseError(response)).isTrue()
        return response.body()
    }

    private fun parseError(response: Response<*>) = if (!response.isSuccessful) response.errorBody().string() else null
}