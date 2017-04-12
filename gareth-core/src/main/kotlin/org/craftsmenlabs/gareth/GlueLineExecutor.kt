package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.validator.model.ExecutionResult
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import java.time.Duration

interface GlueLineExecutor {
    fun executeBaseline(experiment: ExperimentDTO): ExecutionResult
    fun executeAssume(experiment: ExperimentDTO): ExecutionResult
    fun getDuration(experiment: ExperimentDTO): Duration
}
