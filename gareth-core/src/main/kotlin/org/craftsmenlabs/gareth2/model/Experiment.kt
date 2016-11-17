package org.craftsmenlabs.gareth2.model

import java.time.LocalDateTime
import java.util.*

data class Experiment(
        val baseline: String,
        val assumption: String,
        val time: String,
        val success: String,
        val failure: String,
        val value: Int,
        val id: String = UUID.randomUUID().toString()
)

data class ExperimentRun(
        val experimentId: String,
        val baselineExecuted: LocalDateTime,
        val assumptionPlanned: LocalDateTime,
        var assumptionExecuted: LocalDateTime? = null,
        var success: Boolean? = null,
        var completionExecuted: LocalDateTime? = null,
        val id: String = UUID.randomUUID().toString()
)
