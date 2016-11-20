package org.craftsmenlabs.gareth2.model

import java.time.LocalDateTime
import java.util.*

data class Experiment(
        val details: ExperimentDetails,
        val timing: ExperimentTiming,
        val results: ExperimentResults,
        val id: String = UUID.randomUUID().toString()
)

data class ExperimentDetails(
        val baseline: String,
        val assumption: String,
        val time: String,
        val success: String,
        val failure: String,
        val value: Int,
        val id: String
)

data class ExperimentTiming(
        val created: LocalDateTime,
        var ready: LocalDateTime? = null,
        var started: LocalDateTime? = null,
        var waitingForBaseline: LocalDateTime? = null,
        var baselineExecuted: LocalDateTime? = null,
        var waitingForAssume: LocalDateTime? = null,
        var assmueExected: LocalDateTime? = null,
        var waitingFinalizing: LocalDateTime? = null,
        var finalizingExecuted: LocalDateTime? = null
)

data class ExperimentResults(
        var success: Boolean?
)
