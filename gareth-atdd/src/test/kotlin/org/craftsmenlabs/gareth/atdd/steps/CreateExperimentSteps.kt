package org.craftsmenlabs.gareth.atdd.steps

import cucumber.api.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.atdd.CucumberConfig
import org.craftsmenlabs.gareth.atdd.EmbeddedMongoManager
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.rest.ExperimentEndpointClient
import org.craftsmenlabs.gareth.validator.rest.ExperimentTemplateEndpointClient
import org.craftsmenlabs.gareth.validator.rest.OverviewEndpointClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime

@ContextConfiguration(classes = arrayOf(CucumberConfig::class))
open class CreateExperimentSteps {

    val log = LoggerFactory.getLogger("cucumber")
    lateinit var currentTemplate: ExperimentTemplateDTO
    lateinit var currentExperiment: ExperimentDTO
    lateinit var templateCreateDTO: ExperimentTemplateCreateDTO
    lateinit var overviews: List<OverviewDTO>
    lateinit var overviewForTemplate: OverviewDTO

    @Autowired
    private lateinit var overviewClient: OverviewEndpointClient

    @Autowired
    private lateinit var experimentTemplateClient: ExperimentTemplateEndpointClient

    @Autowired
    private lateinit var experimentClient: ExperimentEndpointClient

    @When("^the database is cleared$")
    fun theDatabaseIsCleared() {
        EmbeddedMongoManager.deleteAll()
    }

    @When("^I want to create an experiment named (.*?)$")
    fun iCreateAnExperiment(name: String) {
        templateCreateDTO = ExperimentTemplateCreateDTO(name = name, projectid = "acme", glueLines = Gluelines("", "", ""))
    }

    @When("^the baseline is (.*?)$")
    fun theBaselineIs(baseline: String) {
        templateCreateDTO = templateCreateDTO.copy(glueLines = templateCreateDTO.glueLines.copy(baseline = baseline))
    }

    @When("^the assume is (.*?)$")
    fun theAssumeIs(assume: String) {
        templateCreateDTO = templateCreateDTO.copy(glueLines = templateCreateDTO.glueLines.copy(assume = assume))
    }

    @When("^the success is (.*?)$")
    fun theSuccessIs(success: String) {
        templateCreateDTO = templateCreateDTO.copy(glueLines = templateCreateDTO.glueLines.copy(success = success))
    }

    @When("^the failure is (.*?)$")
    fun theFailureIs(failure: String) {
        templateCreateDTO = templateCreateDTO.copy(glueLines = templateCreateDTO.glueLines.copy(failure = failure))
    }

    @When("^the time is (\\d+) seconds$")
    fun theTimeIs(seconds: Int) {
        templateCreateDTO = templateCreateDTO.copy(glueLines = templateCreateDTO.glueLines.copy(time = "$seconds seconds"))
    }

    @When("^I update the name of the current template to (.*?)$")
    fun iUpdateTheName(name: String) {
        val updateDTO = ExperimentTemplateUpdateDTO(currentTemplate.id, name = name)
        currentTemplate = experimentTemplateClient.update(updateDTO).execute().body()
    }

    @When("^I update the assume line of the current template to (.*?)$")
    fun iUpdateTheAssumeLine(name: String) {
        val updateDTO = ExperimentTemplateUpdateDTO(currentTemplate.id, assume = name)
        currentTemplate = experimentTemplateClient.update(updateDTO).execute().body()
    }

    @When("^I create the template$")
    fun iSubmitTheExperiment() {
        currentTemplate = experimentTemplateClient.create(templateCreateDTO).execute().body()
        assertThat(currentTemplate.id).isNotEmpty()
    }

    @When("^I cannot create the template$")
    fun iCannotCreateTheTemplate() {
        val response = experimentTemplateClient.create(templateCreateDTO).execute()
        assertThat(response.isSuccessful).describedAs("create template should fail").isFalse()
    }

    @When("^the template is correct$")
    fun theTemplateIsReady() {
        assertThat(currentTemplate.ready).isNotNull()
    }

    @When("^the template is not correct$")
    fun theTemplateIsNotReady() {
        assertThat(currentTemplate.ready).isNull()
    }

    @When("^the experiment is running$")
    fun theExperimentIsRunning() {
        refresh()
        assertThat(currentExperiment.status).isEqualTo(ExecutionStatus.RUNNING)
    }

    @When("^the experiment is pending$")
    fun theExperimentIsPending() {
        refresh()
        assertThat(currentExperiment.status).isEqualTo(ExecutionStatus.PENDING)
    }

    @When("^the experiment is completed (|un?)successfully$")
    fun theExperimentIsCompleted(notOk: String) {
        refresh()
        val expectedStatus = if (notOk == "un") "FAILURE" else "SUCCESS"
        assertThat(currentExperiment.completed).isNotNull()
        assertThat(currentExperiment.status.name).isEqualTo(expectedStatus)
    }

    @When("^the experiment is completed$")
    fun theExperimentIsCompleted() {
        refresh()
        assertThat(currentExperiment.completed).isNotNull()
    }

    @When("^I start the experiment immediately$")
    fun iStartTheExperiment() {
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, dueDate = LocalDateTime.now())
        currentExperiment = experimentClient.start(dto).execute().body()
    }

    @When("^I start an experiment for template (.*?) immediately$")
    fun iStartAnExperimentForTemplate(template: String) {
        iStartAnExperimentForTemplateInNSeconds(template, 0)
    }

    @When("^I start an experiment for template (.*?) in (\\d+) seconds?$")
    fun iStartAnExperimentForTemplateInNSeconds(template: String, seconds: Long) {
        currentTemplate = experimentTemplateClient.getByName(template).execute().body()[0]
        val start = if (seconds == 0L) null else LocalDateTime.now().plusSeconds(seconds)
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, dueDate = start)
        currentExperiment = experimentClient.start(dto).execute().body()
    }


    @When("^I cannot start the experiment$")
    fun iCannotStartTheExperiment() {
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, dueDate = LocalDateTime.now())
        val response = experimentClient.start(dto).execute()
        assertThat(response.code()).describedAs("Expected experiment start to fail").isEqualTo(500)
    }

    private fun refresh() {
        currentExperiment = experimentClient.get(currentExperiment.id).execute().body()
    }

    @When("^I wait (\\d+) seconds?$")
    fun iWaitSeconds(seconds: Int) {
        Thread.sleep(1000 * seconds.toLong())
    }

    @When("^the environment key (.*?) has value (.*?)$")
    fun validateKeyAndValue(key: String, value: String) {
        refresh()
        val find = currentExperiment.environment.items.find { it.key == key && it.value == value }
        assertThat(find).describedAs("No key $key with value $value found. Experiment: $currentExperiment.environment.items").isNotNull()
    }

    @When("^I get the overviews for all templates")
    fun iGetTheOverviewForTheCurrentTemplate() {
        overviews = overviewClient.getAllForProject("acme").execute().body()
    }

    @When("^there are (\\d+) templates")
    fun thereAreNTemplates(nmb: Int) {
        assertThat(overviews).describedAs("number of templates in overview").hasSize(nmb)
    }

    @When("^I look at the overview for template (.*?)$")
    fun overviewByName(name: String) {
        overviewForTemplate = overviews.find { it.name == name } ?: throw IllegalStateException("No overview for $name")
    }

    @When("^there (?:is|are) (\\d+) pending runs?$")
    fun numbeOfPending(nmb: Int) {
        assertThat(overviewForTemplate.pending).describedAs("number of pending runs").isEqualTo(nmb)
    }

    @When("^there (?:is|are) (\\d+) current runs?$")
    fun numbeOfRunning(nmb: Int) {
        assertThat(overviewForTemplate.running).describedAs("number of running runs").isEqualTo(nmb)
    }

    @When("^there (?:is|are) (\\d+) failed runs?$")
    fun numberOfFailed(nmb: Int) {
        assertThat(overviewForTemplate.failed).describedAs("number of failed runs").isEqualTo(nmb)
    }

    @When("^there (?:is|are) (\\d+) successful runs?$")
    fun numberOfSuccess(nmb: Int) {
        assertThat(overviewForTemplate.failed).describedAs("number of failed runs").isEqualTo(nmb)
    }

    @When("^the template is (|not )editable?$")
    fun templateIsEditable(yesorNo: String) {
        assertThat(overviewForTemplate.editable).describedAs("is editable").isEqualTo(yesorNo.isBlank())
    }

    @When("^the template is (|not )ready?$")
    fun templateIsReady(yesorNo: String) {
        assertThat(overviewForTemplate.ready).describedAs("is ready").isEqualTo(yesorNo.isBlank())
    }

}