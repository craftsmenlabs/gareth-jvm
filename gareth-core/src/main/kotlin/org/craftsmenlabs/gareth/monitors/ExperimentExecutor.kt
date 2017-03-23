package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.time.TimeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentExecutor @Autowired constructor(private val glueLineExecutor: GlueLineExecutor,
                                                private val dateTimeService: TimeService) {
    private val log = LoggerFactory.getLogger(ExperimentExecutor::class.java)


    fun executeBaseline(experiment: Experiment): Experiment {
        val result = glueLineExecutor.executeBaseline(experiment)
        val now = dateTimeService.now()
        log.info("Executed baseline")
        return experiment.copy(status = result.status,
                environment = result.environment,
                timing = experiment.timing.copy(
                        baselineExecuted = if (result.status == ExecutionStatus.ERROR) null else now))
    }

    fun executeAssume(experiment: Experiment): Experiment {
        val now = dateTimeService.now()
        val result = glueLineExecutor.executeAssume(experiment)
        val assumeExecutedDate = if (result.status == ExecutionStatus.ERROR) null else now
        val executed = experiment.copy(
                timing = experiment.timing.copy(completed = assumeExecutedDate),
                status = result.status,
                environment = result.environment)
        return executed
    }

}

