package org.craftsmenlabs.gareth2.util

import org.craftsmenlabs.gareth.api.execution.ExecutionResult
import org.craftsmenlabs.gareth.api.execution.ExecutionStatus
import org.craftsmenlabs.gareth.api.execution.ExperimentRunEnvironment
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@Profile("test")
open class WrappedGlueLineExecutor : GlueLineExecutor {

    lateinit var mock: GlueLineExecutor

    override fun executeBaseline(experiment: Experiment): ExecutionResult {
        mock.executeBaseline(experiment)
        return ExecutionResult(ExperimentRunEnvironment(listOf()), ExecutionStatus.RUNNING)
    }

    override fun executeAssume(experiment: Experiment): ExecutionResult {
        return mock.executeAssume(experiment)
        return ExecutionResult(ExperimentRunEnvironment(listOf()), ExecutionStatus.SUCCESS)
    }

    override fun getDuration(experiment: Experiment): Duration {
        return mock.getDuration(experiment)
    }

    override fun executeSuccess(experiment: Experiment): ExecutionResult {
        mock.executeSuccess(experiment)
        return ExecutionResult(ExperimentRunEnvironment(listOf()), ExecutionStatus.SUCCESS)
    }

    override fun executeFailure(experiment: Experiment): ExecutionResult {
        mock.executeFailure(experiment)
        return ExecutionResult(ExperimentRunEnvironment(listOf()), ExecutionStatus.FAILURE)
    }
}
