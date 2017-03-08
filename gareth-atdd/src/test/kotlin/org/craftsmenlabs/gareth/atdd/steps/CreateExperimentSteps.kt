package org.craftsmenlabs.gareth.atdd.steps

import cucumber.api.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.atdd.GarethServerEnvironment
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

open class CreateExperimentSteps {

    val log = LoggerFactory.getLogger("cucumber")
    val client: BasicAuthenticationRestClient = BasicAuthenticationRestClient("user", "secret")
    lateinit var currentTemplate: ExperimentTemplateDTO
    lateinit var currentExperiment: ExperimentDTO
    lateinit var templateCreateDTO: ExperimentTemplateCreateDTO

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
        currentTemplate = client.put(updateDTO, ExperimentTemplateDTO::class.java, url("templates"))
    }

    @When("^I update the assume line of the current template to (.*?)$")
    fun iUpdateTheAssumeLine(name: String) {
        val updateDTO = ExperimentTemplateUpdateDTO(currentTemplate.id, assume = name)
        val entity = client.putAsEntity(updateDTO, ExperimentTemplateDTO::class.java, url("templates"))
        if (entity.statusCode.is2xxSuccessful)
            currentTemplate = entity.body
    }

    @When("^I create the template$")
    fun iSubmitTheExperiment() {
        val entity = client.postAsEntity(templateCreateDTO, ExperimentTemplateDTO::class.java, url("templates"))
        if (entity.statusCode.is2xxSuccessful)
            currentTemplate = entity.body
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
        currentExperiment = client.post(dto, ExperimentDTO::class.java, url("experiments"))
    }

    @When("^I cannot start the experiment$")
    fun iCannotStartTheExperiment() {
        val dto = ExperimentCreateDTO(templateId = currentTemplate.id, startDate = LocalDateTime.now())
        val entity = client.postAsEntity(dto, ExperimentDTO::class.java, url("experiments"))
        val statusCode = entity.statusCode
        assertThat(statusCode.is5xxServerError).describedAs("Expected experiment start to fail: ${statusCode.value()}").isTrue()
    }

    private fun refresh() {
        currentExperiment = client.get(ExperimentDTO::class.java, url("experiments/" + currentExperiment.id))
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

    private fun url(path: String) = "http://localhost:${GarethServerEnvironment.garethPort}/gareth/v1/$path"

}