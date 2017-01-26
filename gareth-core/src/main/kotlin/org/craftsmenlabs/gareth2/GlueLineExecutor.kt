package org.craftsmenlabs.gareth2

import org.craftsmenlabs.gareth2.model.Experiment
import java.time.Duration

interface GlueLineExecutor {
    fun executeBaseline(experiment: Experiment)
    fun executeAssume(experiment: Experiment): Boolean
    fun getDuration(experiment: Experiment): Duration
    fun executeSuccess(experiment: Experiment)
    fun executeFailure(experiment: Experiment)
}
