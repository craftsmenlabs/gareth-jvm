package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.model.ExecutionResult
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
@Profile("!mock")
class GlueLineExecutorRestClient(@Autowired private val restClient: ExecutionRestClient) : GlueLineExecutor {

    private val requestFactory = ExecutionRequestFactory()

    override fun executeBaseline(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.BASELINE, requestFactory.createForGluelineType(GlueLineType.BASELINE, experiment))
    }

    override fun executeAssume(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.ASSUME, requestFactory.createForGluelineType(GlueLineType.ASSUME, experiment))
    }

    override fun getDuration(experiment: Experiment): Duration {
        val duration = restClient.getDuration(requestFactory.createForGluelineType(GlueLineType.ASSUME, experiment))
        return Duration.of(duration.amount, ChronoUnit.valueOf(duration.unit))
    }

    override fun executeSuccess(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.SUCCESS, requestFactory.createForGluelineType(GlueLineType.SUCCESS, experiment))
    }

    override fun executeFailure(experiment: Experiment): ExecutionResult {
        return restClient.executeLifeCycleStage(GlueLineType.FAILURE, requestFactory.createForGluelineType(GlueLineType.FAILURE, experiment))
    }

}