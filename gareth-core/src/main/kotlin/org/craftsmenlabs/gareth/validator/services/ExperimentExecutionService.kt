package org.craftsmenlabs.gareth.validator.services

import org.craftsmenlabs.gareth.validator.model.AssumeExecutionResult
import org.craftsmenlabs.gareth.validator.model.BaselineExecutionResult
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus.ERROR
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus.RUNNING
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentExecutionService @Autowired constructor(private val experimentService: ExperimentService,
                                                        private val dateTimeService: TimeService) {
    private val log = LoggerFactory.getLogger(ExperimentExecutionService::class.java)

    fun getBaselinesToExecute(projectId: String): List<ExecutionRequest> {
        val projects = experimentService.getBaselinesDueForProject(projectId)
        return projects.map { ExecutionRequest(it.id, it.runContext, it.glueLines) }
    }

    fun setBaselineExecutionResult(result: BaselineExecutionResult) {
        val experiment = experimentService.getExperimentById(result.experimentId)
        val now = dateTimeService.now()
        log.info("Executed baseline. Result: " + result.success)
        val mustAbort = !result.success || result.assumptionDue == null
        val copy = experiment.copy(status = if (mustAbort) ERROR else RUNNING,
                runContext = result.runContext,
                completed = if (mustAbort) dateTimeService.now() else null,
                assumeDue = result.assumptionDue,
                baselineExecuted = if (mustAbort) null else now)
        experimentService.updateExperiment(copy)

    }

    fun getAssumesToExecute(projectId: String): List<ExecutionRequest> {
        val projects = experimentService.getAssumesDueForProject(projectId)
        return projects.map { ExecutionRequest(it.id, it.runContext, it.glueLines) }
    }

    fun setAssumeExecutionResult(result: AssumeExecutionResult) {
        val experiment = experimentService.getExperimentById(result.experimentId)
        log.info("Executed baseline. Result: " + result.status.name)
        val executed = experiment.copy(
                completed = dateTimeService.now(),
                status = result.status,
                runContext = result.runContext)
        experimentService.updateExperiment(executed)
    }

}

