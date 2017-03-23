package org.craftsmenlabs.gareth.client

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.ExecutionRequest
import org.craftsmenlabs.gareth.model.ExecutionResult
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
@Profile("!mock")
class GlueLineExecutorRestClient(@Autowired private val restClient: ExecutionRestClient) : GlueLineExecutor {

    override fun executeBaseline(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.BASELINE, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun executeAssume(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.ASSUME, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun getDuration(experiment: Experiment): Duration {
        val duration = restClient.getDuration(ExecutionRequest(experiment.environment, experiment.glueLines))
        return Duration.of(duration.amount, ChronoUnit.valueOf(duration.unit))
    }

}