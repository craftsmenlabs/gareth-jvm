package org.craftsmenlabs.gareth.execution.services.definitions

import mockit.*
import org.craftsmenlabs.gareth.execution.services.ExecutionService
import org.craftsmenlabs.gareth.execution.services.ExperimentExecutionManager
import org.craftsmenlabs.gareth.execution.services.RunningExecutionJobsCache
import org.craftsmenlabs.gareth.validator.model.AssumeExecutionResult
import org.craftsmenlabs.gareth.validator.model.BaselineExecutionResult
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.rest.GarethHubClient
import org.junit.Before
import org.junit.Test
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor

class ExperimentExecutionManagerTest {

    //use a plain single-thread implementation for this test
    @Injectable
    val taskExecutor: TaskExecutor = SyncTaskExecutor()
    @Injectable
    lateinit var executionService: ExecutionService
    @Injectable
    lateinit var endpointClient: GarethHubClient
    @Injectable
    lateinit var cache: RunningExecutionJobsCache

    @Before
    fun setup() {
        Deencapsulation.setField(client, "clientId", "ACME")
    }

    @Tested(availableDuringSetup = true)
    lateinit var client: ExperimentExecutionManager

    @Test
    fun testRunAssumeTasks(@Injectable request: ExecutionRequest, @Injectable executionResult: AssumeExecutionResult) {
        object : Expectations() {
            init {
                request.experimentId
                result = "ACME"
                endpointClient.getAssumesToExecute("ACME")
                result = listOf(request)
                executionService.executeAssumption(request)
                result = executionResult
            }
        }
        client.runAssumeTasks()
        object : Verifications() {
            init {
                cache.registerExperiment("ACME")
                endpointClient.updateAssumeStatus(executionResult)
                cache.popExperiment("ACME")
            }
        }
    }

    @Test
    fun testRunBaselineTasks(@Injectable request: ExecutionRequest, @Injectable executionResult: BaselineExecutionResult) {
        object : Expectations() {
            init {
                request.experimentId
                result = "ACME"
                endpointClient.getBaselinesToExecute("ACME")
                result = listOf(request)
                executionService.executeBaseline(request)
                result = executionResult
            }
        }
        client.runBaselineTasks()
        object : Verifications() {
            init {
                cache.registerExperiment("ACME")
                endpointClient.updateBaselineStatus(executionResult)
                cache.popExperiment("ACME")
            }
        }
    }

    @Test
    fun testRunBaselineTasksWillSkip(@Injectable request: ExecutionRequest, @Injectable executionResult: BaselineExecutionResult) {
        object : Expectations() {
            init {
                request.experimentId
                result = "ACME"
                cache.canSkip("ACME")
                result = true
                endpointClient.getBaselinesToExecute("ACME")
                result = listOf(request)
            }
        }
        client.runBaselineTasks()
        object : Verifications() {
            init {
                executionService.executeBaseline(request)
                maxTimes = 0
                cache.registerExperiment("ACME")
                maxTimes = 0
                endpointClient.updateBaselineStatus(executionResult)
                maxTimes = 0
                cache.popExperiment("ACME")
                maxTimes = 0
            }
        }
    }

}