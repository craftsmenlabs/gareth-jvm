package org.craftsmenlabs.gareth2.atdd.steps

import cucumber.api.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.api.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.api.model.ExperimentDTO
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.slf4j.LoggerFactory

open class CreateExperimentSteps {

    val log = LoggerFactory.getLogger("cucumber")
    val client: BasicAuthenticationRestClient = BasicAuthenticationRestClient("user", "secret")
    lateinit var currentExperiment: ExperimentDTO
    lateinit var experiment: ExperimentCreateDTO

    @When("^I want to create an experiment named (.*?)$")
    fun iCreateAnExperiment(name: String) {
        experiment = ExperimentCreateDTO()
        experiment.name = name
    }

    @When("^the baseline is (.*?)$")
    fun theBaselineIs(baseline: String) {
        experiment.baseline = baseline
    }

    @When("^the assume is (.*?)$")
    fun theAssumeIs(assume: String) {
        experiment.assume = assume
    }

    @When("^the success is (.*?)$")
    fun theSuccessIs(success: String) {
        experiment.success = success
    }

    @When("^the failure is (.*?)$")
    fun theFailureIs(failure: String) {
        experiment.failure = failure
    }

    @When("^the time is (\\d+) seconds$")
    fun theTimeIs(seconds: Int) {
        experiment.time = "$seconds seconds"
    }

    @When("^I submit the experiment$")
    fun iSubmitTheExperiment() {
        currentExperiment = client.put(experiment, ExperimentDTO::class.java, url(""))
    }

    @When("^the experiment is created$")
    fun theExperimentIsCreated() {
        assertThat(currentExperiment.created).isNotNull()
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

    @When("^the experiment is completed$")
    fun theExperimentIsCompleted() {
        refresh()
        assertThat(currentExperiment.completed).isNotNull()
    }

    @When("^I start the experiment$")
    fun iStartTheExperiment() {
        currentExperiment = client.put(experiment, ExperimentDTO::class.java, url("${currentExperiment.id}/start"))
    }

    private fun refresh() {
        currentExperiment = client.get(ExperimentDTO::class.java, url("/" + currentExperiment.id))
    }

    @When("^I wait (\\d+) seconds$")
    fun iWaitSeconds(seconds: Int) {
        Thread.sleep(1000 * seconds.toLong())
    }

    @When("^the environment key (.*?) has value (.*?)$")
    fun validateKeyAndValue(key: String, value: String) {
        refresh()
        assertThat(currentExperiment.environment).describedAs("No key $key with value $value found").containsEntry(key, value)
    }

    private fun url(path: String) = "http://localhost:8090/gareth/v1/experiments/$path"

}