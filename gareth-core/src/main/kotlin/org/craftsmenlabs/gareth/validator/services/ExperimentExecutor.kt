package org.craftsmenlabs.gareth.validator.services

import org.craftsmenlabs.gareth.validator.client.GlueLineExecutor
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.time.TimeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentExecutor @Autowired constructor(private val glueLineExecutor: GlueLineExecutor,
                                                private val dateTimeService: TimeService) {
    private val log = LoggerFactory.getLogger(ExperimentExecutor::class.java)


    fun executeBaseline(experiment: ExperimentDTO): ExperimentDTO {
        val result = glueLineExecutor.executeBaseline(experiment)
        val now = dateTimeService.now()
        log.info("Executed baseline. Result: " + result.status.name)
        val mustAbort = result.status == ExecutionStatus.ERROR
        return experiment.copy(status = result.status,
                environment = result.environment,
                completed = if (mustAbort) dateTimeService.now() else null,
                baselineExecuted = if (mustAbort) null else now)
    }

    fun executeAssume(experiment: ExperimentDTO): ExperimentDTO {
        if (experiment.status != ExecutionStatus.RUNNING)
            throw IllegalStateException("Can only execute assume when experiment is in state RUNNING, but state is ${experiment.status.name}")
        val result = glueLineExecutor.executeAssume(experiment)
        val executed = experiment.copy(
                completed = dateTimeService.now(),
                status = result.status,
                environment = result.environment)
        return executed
    }

}

