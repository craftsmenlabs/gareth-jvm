package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.rest.GarethHubClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service

@Service
class ExperimentExecutionManager @Autowired constructor(
        private val taskExecutor: TaskExecutor,
        private val cache: RunningExecutionJobsCache,
        private val executionService: ExecutionService,
        private val endpointClient: GarethHubClient) {
    @Value("client.id")
    private lateinit var clientId: String
    private val log: Logger = LoggerFactory.getLogger(JobScheduler::class.java)


    fun runAssumeTasks() {
        val assumesToExecute = endpointClient.getAssumesToExecute(clientId)
        log.info("Running ${assumesToExecute.size} assumption tasks")
        assumesToExecute.forEach {
            scheduleTask(it.experimentId, {
                val result = executionService.executeAssumption(it)
                log.info("result of assumption invocation for experiment ${result.experimentId} ${result.status}")
                endpointClient.updateAssumeStatus(result)
            })
        }
    }

    fun runBaselineTasks() {
        val baselinesToExecute = endpointClient.getBaselinesToExecute(clientId)
        log.info("Running ${baselinesToExecute.size} baseline tasks")
        baselinesToExecute.forEach {
            scheduleTask(it.experimentId, {
                val result = executionService.executeBaseline(it)
                log.info("Success of baseline invocation for experiment ${result.experimentId}: ${result.success}")
                endpointClient.updateBaselineStatus(result)
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