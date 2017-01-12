package org.craftsmenlabs.gareth2.glueline

import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.model.Experiment
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("stub")
open class StubGlueLineExecutor : GlueLineExecutor {
    override fun executeBaseline(experiment: Experiment) {
    }

    override fun executeAssumption(experiment: Experiment): Boolean {
        return false
    }

    override fun getDuration(experiment: Experiment): Duration {
        return Duration.ZERO
    }

    override fun executeSuccess(experiment: Experiment) {
    }

    override fun executeFailure(experiment: Experiment) {
    }
}
