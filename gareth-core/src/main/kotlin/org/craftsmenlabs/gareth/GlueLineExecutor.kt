package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.ExecutionResult
import org.craftsmenlabs.gareth.model.Experiment
import java.time.Duration

interface GlueLineExecutor {
    fun executeBaseline(experiment: Experiment): ExecutionResult
    fun executeAssume(experiment: Experiment): ExecutionResult
    fun getDuration(experiment: Experiment): Duration
}
