package org.craftsmenlabs.gareth.execution.services.definitions

import org.craftsmenlabs.gareth.execution.services.ExecutionService
import org.craftsmenlabs.gareth.execution.services.RunningExecutionJobsCache
import org.craftsmenlabs.gareth.validator.rest.ExecutionEndpointClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service

@Service
class GarethHubClient(@Autowired
                      private val taskExecutor: TaskExecutor,
                      private val cache: RunningExecutionJobsCache,
                      private val executionService: ExecutionService,
                      private val endpointClient: ExecutionEndpointClient) {
    @Value("client.id")
    private lateinit var clientId: String

    fun runAssumeTasks() {
        val assumesToExecute = endpointClient.getAssumesToExecute(clientId)
        assumesToExecute.forEach {
            scheduleTask(it.experimentId, {
                endpointClient.updateAssumeStatus(executionService.executeAssumption(it))
            })
        }
    }

    fun runBaselineTasks() {
        val baselinesToExecute = endpointClient.getBaselinesToExecute(clientId)
        baselinesToExecute.forEach {
            scheduleTask(it.experimentId, {
                endpointClient.updateBaselineStatus(executionService.executeBaseline(it))
            })
        }
    }

    private fun scheduleTask(experimentId: String, executable: () -> Unit) {
        if (cache.canSkip(experimentId)) {
            return
        } else {
            cache.registerExperiment(experimentId)
        }
        try {
            taskExecutor.execute(executable)
        } finally {
            cache.popExperiment(experimentId)
        }
    }


}