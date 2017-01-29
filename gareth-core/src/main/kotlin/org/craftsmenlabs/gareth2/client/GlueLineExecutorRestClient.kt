package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.api.execution.ExecutionStatus
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class GlueLineExecutorRestClient(@Autowired private val restClient: ExecutionRestClient) : GlueLineExecutor {

    private val requestFactory = ExecutionRequestFactory()

    override fun executeBaseline(experiment: Experiment) {
        restClient.executeLifeCycleStage(GlueLineType.BASELINE, requestFactory.createForGluelineType(GlueLineType.BASELINE, experiment))
    }

    override fun executeAssume(experiment: Experiment): Boolean {
        val result = restClient.executeLifeCycleStage(GlueLineType.ASSUME, requestFactory.createForGluelineType(GlueLineType.ASSUME, experiment))
        return result.status == ExecutionStatus.SUCCESS
    }

    override fun getDuration(experiment: Experiment): Duration {
        val duration = restClient.getDuration(requestFactory.createForGluelineType(GlueLineType.ASSUME, experiment))
        return Duration.of(duration.amount, ChronoUnit.valueOf(duration.unit))
    }

    override fun executeSuccess(experiment: Experiment) {
        restClient.executeLifeCycleStage(GlueLineType.SUCCESS, requestFactory.createForGluelineType(GlueLineType.SUCCESS, experiment))
    }

    override fun executeFailure(experiment: Experiment) {
        restClient.executeLifeCycleStage(GlueLineType.FAILURE, requestFactory.createForGluelineType(GlueLineType.FAILURE, experiment))
    }

}