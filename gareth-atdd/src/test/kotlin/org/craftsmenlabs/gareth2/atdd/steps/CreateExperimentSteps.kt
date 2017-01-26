package org.craftsmenlabs.gareth2.atdd.steps

import cucumber.api.java.en.When
import org.slf4j.LoggerFactory

open class CreateExperimentSteps {

    val log = LoggerFactory.getLogger("cucumber")

    @When("^I want to create an experiment$")
    fun iCreateAnExperiment() {
        log.info("creating experiment")
    }

    @When("^the baseline is (.*?)$")
    fun theBaselineIs(baseline: String) {
        log.info("baseline is $baseline")
    }

    @When("^the assume is (.*?)$")
    fun theAssumeIs(assume: String) {
        log.info("assume is $assume")
    }

    @When("^the success is (.*?)$")
    fun theSuccessIs(success: String) {
        log.info("success is $success")
    }

    @When("^the failure is (.*?)$")
    fun theFailureIs(failure: String) {
        log.info("failure is $failure")
    }

    @When("^the time is (\\d+) seconds$")
    fun theTimeIs(seconds: Int) {
        log.info("time is $seconds")
    }

    @When("^I submit the experiment$")
    fun iSubmitTheExperiment() {
        log.info("I submit the experiment")
    }

    @When("^the experiment is created$")
    fun theExperimentIsCreated() {
        log.info("The experiment is created")
    }

}