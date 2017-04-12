package org.craftsmenlabs.gareth.validator.client

import org.craftsmenlabs.gareth.validator.GlueLineExecutor
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.model.ExecutionResult
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
@Profile("!mock")
class GlueLineExecutorRestClient(@Autowired private val restClient: ExecutionRestClient) : GlueLineExecutor {

    override fun executeBaseline(experiment: ExperimentDTO): ExecutionResult {
        return restClient. executeLifeCycleStage(GlueLineType.BASELINE, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun executeAssume(experiment: ExperimentDTO): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.ASSUME, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun getDuration(experiment: ExperimentDTO): Duration {
        val duration = restClient.getDuration(ExecutionRequest(experiment.environment, experiment.glueLines))
        return Duration.of(duration.amount, ChronoUnit.valueOf(duration.unit))
    }

}