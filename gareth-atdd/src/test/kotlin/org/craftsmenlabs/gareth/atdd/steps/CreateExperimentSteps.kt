package org.craftsmenlabs.gareth.atdd.steps

import cucumber.api.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.atdd.CucumberConfig
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.ExperimentEndpointClient
import org.craftsmenlabs.gareth.rest.ExperimentTemplateEndpointClient
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
    var overviewForTemplate: OverviewDTO? = null

    @Autowired
    private lateinit var experimentTemplateClient: ExperimentTemplateEndpointClient

    @Autowired
    private lateinit var experimentClient: ExperimentEndpointClient

    @When("^I want to create an experiment named (.*?)$")
    fun iCreateAnExperiment(name: String) {
        templateCreateDTO = ExperimentTemplateCreateDTO(name = name, glueLines = Gluelines("", "", "", "", ""))
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
    }

    @When("^the template is correct$")
    fun theTemplateIsReady() {
        assertThat(currentTemplate.ready).isNotNull()
    }

    @When("^the template is not correct$")
    fun theTemplateIsNotReady() {
        assertThat(currentTemplate.ready).isNull()
    }

    @When("^the experiment is ready$")
    fun theExperimentIsReady() {
        refresh()
        assertThat(currentExperiment.ready).isNotNull()
    }

    @When("^the experiment is started$")
    fun theExperimentIsStarted() {
        refresh()
        assertThat(currentExperiment.started).isNotNull()
    }

    @When("^the experiment is completed (|un?)successfully$")
    fun theExperimentIsCompleted(notOk: String) {
        refresh()
        val expectedStatus = if (notOk == "un") "FAILURE" else "SUCCESS"
        assertThat(currentExperiment.completed).isNotNull()
        assertThat(currentExperiment.result.name).isEqualTo(expectedStatus)
    }

    @When("^I start the experiment$")
    fun iStartTheExperiment() {
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, startDate = LocalDateTime.now())
        currentExperiment = experimentClient.start(dto).execute().body()
    }

    @When("^I cannot start the experiment$")
    fun iCannotStartTheExperiment() {
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, startDate = LocalDateTime.now())
        val response = experimentClient.start(dto).execute()
        assertThat(response.code()).describedAs("Expected experiment start to fail").isEqualTo(500)
    }

    private fun refresh() {
        currentExperiment = experimentClient.get(currentExperiment.id).execute().body()
    }

    @When("^I wait (\\d+) seconds$")
    fun iWaitSeconds(seconds: Int) {
        Thread.sleep(1000 * seconds.toLong())
    }

    @When("^the environment key (.*?) has value (.*?)$")
    fun validateKeyAndValue(key: String, value: String) {
        refresh()
        val find = currentExperiment.environment.items.find { it.key == key && it.value == value }
        assertThat(find).describedAs("No key $key with value $value found. Experiment: $currentExperiment.environment.items").isNotNull()
    }

    @When("^I get the overview for the current template$")
    fun iGetTheOverviewForTheCurrentTemplate() {

    }


}