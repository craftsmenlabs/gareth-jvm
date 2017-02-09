package org.craftsmenlabs.gareth.atdd.steps

import cucumber.api.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.atdd.CucumberConfig
import org.craftsmenlabs.gareth.atdd.GarethServerEnvironment
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.slf4j.LoggerFactory
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = arrayOf(CucumberConfig::class))
open class ExecuteExperimentSteps {

    val log = LoggerFactory.getLogger("cucumber")

    lateinit var latestResult: ExecutionResult

    val client: BasicAuthenticationRestClient = BasicAuthenticationRestClient("user", "secret")

    @When("^I set the baseline to (.*?)$")
    fun executeBaseline(baseline: String) {
        val environment = ExperimentRunEnvironment(listOf(EnvironmentItem("ping", "pong", ItemType.STRING)))
        val request = ExecutionRequest(environment, baseline)
        latestResult = client.put(request, ExecutionResult::class.java, url("baseline"))
    }

    @When("^I validate the assumption (.*?)$")
    fun validateAssumption(assume: String) {
        val request = ExecutionRequest(latestResult.environment, assume)
        latestResult = client.put(request, ExecutionResult::class.java, url("assume"))
        log.info("validated assumption ${latestResult.status}")
    }

    @When("^the assumption is(| not) successful$")
    fun isSuccessFul(yesOrNo: String) {
        val expected = if (yesOrNo.isEmpty()) ExecutionStatus.SUCCESS else ExecutionStatus.FAILURE
        assertThat(latestResult.status).isEqualTo(expected)
    }

    @When("^I execute the (success|failure) step (.*?)$")
    fun successOrFailure(successOrFailure: String, glueLine: String) {
        val request = ExecutionRequest(latestResult.environment, glueLine)
        latestResult = client.put(request, ExecutionResult::class.java, url(successOrFailure))
    }

    @When("^the experiment environment key (.*?) has value (.*?)$")
    fun validateKeyAndValue(key: String, value: String) {
        val find = latestResult.environment.items.find { it.key == key && it.value == value }
        assertThat(find).describedAs("No key $key with value $value found").isNotNull()
    }

    private fun url(path: String) = "http://localhost:${GarethServerEnvironment.executionPort}/gareth/v1/$path"

}