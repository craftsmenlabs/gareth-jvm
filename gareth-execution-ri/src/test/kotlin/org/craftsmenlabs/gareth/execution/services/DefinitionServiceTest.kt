package org.craftsmenlabs.gareth.execution.services

import mockit.Expectations
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.api.execution.*
import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class DefinitionServiceTest {

    @Injectable
    lateinit var registry: DefinitionRegistry

    @Tested
    lateinit var definitionService: DefinitionService

    val environment = ExperimentRunEnvironment(listOf(EnvironmentItem("sale", "42", ItemType.LONG)))

    @Test
    fun testBaseline() {
        var request = ExecutionRequest(environment, "sale of apples")
        object : Expectations() {
            init {
                registry.invokeVoidMethodByType("sale of apples", ExecutionType.BASELINE, request)
                result = RunContext.create(request)
            }
        }
        val response = definitionService.executeBaseline(request)
        assertThat(response.status).isEqualTo(ExecutionStatus.RUNNING)
    }


    @Test
    fun testSuccessAssume() {
        var request = ExecutionRequest(environment, "has risen")
        object : Expectations() {
            init {
                registry.invokeAssumptionMethod("has risen", request)
                result = Pair(true, RunContext.create(request))
            }
        }
        val response = definitionService.executeAssumption(request)
        assertThat(response.status).isEqualTo(ExecutionStatus.SUCCESS)
    }


    @Test
    fun testFailureAssume() {
        var request = ExecutionRequest(environment, "has risen")
        object : Expectations() {
            init {
                registry.invokeAssumptionMethod("has risen", request)
                result = Pair(false, RunContext.create(request))
            }
        }
        val response = definitionService.executeAssumption(request)
        assertThat(response.status).isEqualTo(ExecutionStatus.FAILURE)
    }

    @Test
    fun testSuccess() {
        var request = ExecutionRequest(environment, "send cake")
        object : Expectations() {
            init {
                registry.invokeVoidMethodByType("send cake", ExecutionType.SUCCESS, request)
                result = RunContext.create(request)
            }
        }
        val response = definitionService.executeSuccess(request)
        assertThat(response.status).isEqualTo(ExecutionStatus.SUCCESS)
    }

    @Test
    fun testFailure() {
        var request = ExecutionRequest(environment, "fire the culprit")
        object : Expectations() {
            init {
                registry.invokeVoidMethodByType("fire the culprit", ExecutionType.FAILURE, request)
                result = RunContext.create(request)
            }
        }
        val response = definitionService.executeFailure(request)
        assertThat(response.status).isEqualTo(ExecutionStatus.FAILURE)
    }
}