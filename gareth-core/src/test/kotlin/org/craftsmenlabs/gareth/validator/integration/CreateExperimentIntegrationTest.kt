package org.craftsmenlabs.gareth.validator.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.client.GluelineValidator
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.services.ExperimentService
import org.craftsmenlabs.gareth.validator.services.OverviewService
import org.craftsmenlabs.gareth.validator.services.TemplateService
import org.craftsmenlabs.gareth.validator.time.DateFormatUtils
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(TestConfig::class, EmbeddedMongoAutoConfiguration::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
class CreateExperimentIntegrationTest {

    @Autowired
    lateinit var experimentService: ExperimentService

    @Autowired
    lateinit var templateService: TemplateService

    @Autowired
    lateinit var overviewService: OverviewService

    @Autowired
    lateinit var validator: GluelineValidator

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
            val response = validator.lookupGlueline("acme", glueLine, content)
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

        val created = experimentService.createExperiment(ExperimentCreateDTO(template.id, null))
        Thread.sleep(1000)
        assertThat(created.id).isNotNull()
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.name).isEqualTo("Goodbye world")
        assertThat(saved.created).isNotNull()
    }

    @Test
    fun b_createExperimentWithoutStartDate() {
        val createTemplateDTO = createFullTemplate("Hello world")
        val template = createTemplate(createTemplateDTO)
        val experimentCreateDTO = ExperimentCreateDTO(templateId = template.id, environment = ExperimentRunEnvironment(listOf(EnvironmentItem("COMPANY_TOKEN", "ABC", ItemType.STRING))))
        val created = postExperiment(experimentCreateDTO)
        Thread.sleep(1000)
        assertThat(created.id).isNotNull()
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.environment.items.map { it.key }).containsOnly("COMPANY_TOKEN")
        assertThat(saved.name).isEqualTo("Hello world")
        assertThat(saved.created).isNotNull()
    }

    @Test
    fun c_createExperimentWithFaultyBaseline() {
        val orig = createFullTemplate("Hello world2")
        val dto = orig.copy(glueLines = orig.glueLines.copy(baseline = "sale of computers"))
        val template = createTemplate(dto)
        assertThat(template.id).isNotEmpty()
        val saved = templateService.getTemplateById(template.id)
        assertThat(saved.name).isEqualTo("Hello world2")
        assertThat(saved.ready).isNull()
        val updated = updateTemplate(ExperimentTemplateUpdateDTO(id = saved.id, assume = "sale of fruit"))
        assertThat(updated.ready).isNotNull()
    }

    @Test
    fun d_createAndStartExperiment() {
        val template = createTemplate(createFullTemplate("Hello world3"))
        val dto = ExperimentCreateDTO(templateId = template.id, dueDate = DateTimeDTO(LocalDateTime.now().plusSeconds(3)))
        val created = postExperiment(dto)
        Thread.sleep(500)
        assertThat(experimentService.getExperimentById(created.id).status).isEqualTo(ExecutionStatus.PENDING)
        Thread.sleep(2000)

        var saved = experimentService.getExperimentById(created.id)
        assertThat(saved.name).isEqualTo("Hello world3")
        assertThat(saved.status).isEqualTo(ExecutionStatus.RUNNING)

        Thread.sleep(1000)
        saved = experimentService.getExperimentById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.completed).isNotNull()
    }

    @Test
    fun d_createAndStartExperimentWithoutFinalization() {
        val template = createTemplate(createTemplateWithoutFinalization("Hello world4"))
        val dto = ExperimentCreateDTO(templateId = template.id, dueDate = null)
        val created = postExperiment(dto)
        Thread.sleep(2000)
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(saved.completed).isNotNull()
    }

    @Test
    fun ea_createAndStartExperimentWithoutFinalization() {
        var dto = createTemplateWithoutFinalization("Failed baseline")
        dto = dto.copy(glueLines = dto.glueLines.copy(assume = "THROW"))
        val template = createTemplate(dto)
        val experimentDTO = ExperimentCreateDTO(templateId = template.id, dueDate = null)
        val created = postExperiment(experimentDTO)
        Thread.sleep(2000)
        val saved = experimentService.getExperimentById(created.id)
        assertThat(saved.status).isEqualTo(ExecutionStatus.ERROR)
        assertThat(saved.completed).isNotNull()
    }

    @Test
    fun eb_testRetrieval() {
        fun getRuns(templateName: String,
                    createdAfter: String? = null,
                    status: ExecutionStatus? = null,
                    completed: Boolean? = null): List<ExperimentDTO> {
            val experiments = experimentService.getFiltered("acme", createdAfter, status, completed)
            return experiments.filter { it.name == templateName }
        }
        assertThat(getRuns(templateName = "Hello world")).hasSize(1)
        assertThat(getRuns("Hello world", completed = true)).hasSize(1)
        assertThat(getRuns("Hello world", completed = true, status = ExecutionStatus.SUCCESS)).hasSize(1)

        assertThat(getRuns("Failed baseline", completed = true, status = ExecutionStatus.ERROR)).hasSize(1)
    }

    @Test
    fun f_testarchiving() {
        val toArchive = createTemplate(createFullTemplate("to be archived"))
        assertThat(overviewService.getAllForProject("acme").map { it.name }).contains("to be archived")
        templateService.update(ExperimentTemplateUpdateDTO(id = toArchive.id, archived = true))
        assertThat(overviewService.getAllForProject("acme").map { it.name }).doesNotContain("to be archived")
    }


    private fun createFullTemplate(name: String): ExperimentTemplateCreateDTO {
        return ExperimentTemplateCreateDTO(name = name, projectid = "acme",
                glueLines = Gluelines(
                        baseline = "sale of fruit",
                        assume = "sale of fruit has risen by 81 per cent",
                        time = "2 seconds",
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
                        time = "1 seconds"
                ),
                value = 42)
    }


    private fun createTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        return templateService.create(dto)
    }

    private fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        return templateService.update(dto)
    }

    private fun postExperiment(dto: ExperimentCreateDTO): ExperimentDTO {
        return experimentService.createExperiment(dto)
    }
}