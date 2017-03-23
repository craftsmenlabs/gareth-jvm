package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.ExecutionResult
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentRunEnvironment
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@Profile("mock")
open class WrappedGlueLineExecutor : GlueLineExecutor {

    lateinit var mock: GlueLineExecutor

    override fun executeBaseline(experiment: Experiment): ExecutionResult {
        mock.executeBaseline(experiment)
        return ExecutionResult(ExperimentRunEnvironment(), ExecutionStatus.RUNNING)
    }

    override fun executeAssume(experiment: Experiment): ExecutionResult {
        return mock.executeAssume(experiment)
        return ExecutionResult(ExperimentRunEnvironment(), ExecutionStatus.SUCCESS)
    }

    override fun getDuration(experiment: Experiment): Duration {
        return mock.getDuration(experiment)
    }

}
