package org.craftsmenlabs.gareth.validator.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.services.ExperimentService
import org.craftsmenlabs.gareth.services.GluelineService
import org.craftsmenlabs.gareth.services.TemplateService
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.gareth.validator.model.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import retrofit2.Response
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(TestConfig::class, EmbeddedMongoAutoConfiguration::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CreateExperimentIntegrationTest {

    @Autowired
    lateinit var experimentService: ExperimentService

    @Autowired
    lateinit var templateService: TemplateService

    @Autowired
    lateinit var gluelineService: GluelineService

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
            val response = gluelineService.lookupGlueline(glueLine, content)
            assertThat(response.exact).isEqualTo(content)
            assertThat(response.suggestions).containsExactly(content)
        }
        getGlueLine(GlueLineType.BASELINE, "get sale of fruit")
        getGlueLine(GlueLineType.ASSUME, "has risen")
        getGlueLine(GlueLineType.SUCCESS, "send mail to Moos")
        getGlueLine(GlueLineType.FAILURE, "send mail to Sam")
        getGlueLine(GlueLineType.TIME, "3 weeks")

    }

    @Test
    fun b_createExperimentWithoutStartDate1() {
        val createTemplateDTO = createFullTemplate("Goodbye world")
        val template = templateService.create(createTemplateDTO)

        val created = experimentService.createExperiment(ExperimentCreateDTO(template.id, LocalDateTime.now()))
        Thread.sleep(2000)
        assertThat(created.id).isNotNull()
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.name).isEqualTo("Goodbye world")
        assertThat(saved.created).isNotNull()

    }


    @Test
    fun b_createExperimentWithoutStartDate() {
        val createTemplateDTO = createFullTemplate("Hello world")
        val template = postTemplate(createTemplateDTO)
        val experimentCreateDTO = ExperimentCreateDTO(templateId = template.id)
        val created = postExperiment(experimentCreateDTO)
        Thread.sleep(2000)
        assertThat(created.id).isNotNull()
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.created).isNotNull()

    }

    @Test
    fun c_createExperimentWithFaultyBaseline() {
        val orig = createFullTemplate("Hello world2")
        val dto = orig.copy(glueLines = orig.glueLines.copy(baseline = "sale of computers"))
        val template = postTemplate(dto)
        assertThat(template.id).isNotEmpty()
        val saved = templateService.getTemplateById(template.id)
        assertThat(saved.name).isEqualTo("Hello world2")
        assertThat(saved.ready).isNull()
        val updated = updateTemplate(ExperimentTemplateUpdateDTO(id = saved.id, assume = "sale of fruit"))
        assertThat(updated.ready).isNotNull()
    }

    @Test
    fun d_createAndStartExperiment() {
        val template = postTemplate(createFullTemplate("Hello world3"))
        val dto = ExperimentCreateDTO(templateId = template.id, dueDate = LocalDateTime.now().plusSeconds(5))
        val created = postExperiment(dto)
        assertThat(experimentService.getExperimentById(created.id).status).isEqualTo(ExecutionStatus.PENDING)
        Thread.sleep(5000)

        var saved = experimentService.getExperimentById(created.id)
        assertThat(saved.name).isEqualTo("Hello world3")
        assertThat(saved.status).isEqualTo(ExecutionStatus.RUNNING)

        Thread.sleep(5000)
        saved = experimentService.getExperimentById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.completed).isNotNull()
    }

    @Test
    fun d_createAndStartExperimentWithoutFinalization() {
        val template = postTemplate(createTemplateWithoutFinalization("Hello world4"))
        val dto = ExperimentCreateDTO(templateId = template.id, dueDate = LocalDateTime.now())
        val created = postExperiment(dto)
        Thread.sleep(3000)
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.completed).isNotNull()

    }

    private fun createFullTemplate(name: String): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = name, projectid = "acme",
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "5 seconds",
                        success = "send email to Sam",
                        failure = "send email to Moos"
                ),
                value = 42)
    }

    private fun createTemplateWithoutFinalization(name: String): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = name, projectid = "acme",
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "2 seconds"
                ),
                value = 42)
    }


    private fun postTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        return templateService.create(dto)
    }

    private fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        return templateService.update(dto)
    }

    private fun postExperiment(dto: ExperimentCreateDTO): ExperimentDTO {
        return experimentService.createExperiment(dto)
    }

    private fun searchExperiment(date: String? = null, completed: Boolean? = null): List<ExperimentDTO> {
        return experimentService.getFiltered("acme", date, completed)
    }

    private fun parseError(response: Response<*>) = if (!response.isSuccessful) response.errorBody().string() else null
}