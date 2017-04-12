package org.craftsmenlabs.gareth.validator.monitors

import org.craftsmenlabs.gareth.validator.GlueLineExecutor
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
        log.info("Executed baseline")
        return experiment.copy(status = result.status,
                environment = result.environment,
                baselineExecuted = if (result.status == ExecutionStatus.ERROR) null else now)
    }

    fun executeAssume(experiment: ExperimentDTO): ExperimentDTO {
        val now = dateTimeService.now()
        val result = glueLineExecutor.executeAssume(experiment)
        val assumeExecutedDate = if (result.status == ExecutionStatus.ERROR) null else now
        val executed = experiment.copy(
                completed = assumeExecutedDate,
                status = result.status,
                environment = result.environment)
        return executed
    }

}

