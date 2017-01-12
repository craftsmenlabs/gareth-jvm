package org.craftsmenlabs.gareth2.util

import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@Profile("test")
open class WrappedGlueLineExecutor : GlueLineExecutor {

    lateinit var mock: GlueLineExecutor

    override fun executeBaseline(experiment: Experiment) {
        mock.executeBaseline(experiment)
    }

    override fun executeAssumption(experiment: Experiment): Boolean {
        return mock.executeAssumption(experiment)
    }

    override fun getDuration(experiment: Experiment): Duration {
        return mock.getDuration(experiment)
    }

    override fun executeSuccess(experiment: Experiment) {
        mock.executeSuccess(experiment)
    }

    override fun executeFailure(experiment: Experiment) {
        mock.executeFailure(experiment)
    }
}
