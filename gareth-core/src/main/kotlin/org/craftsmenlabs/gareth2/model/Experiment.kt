package org.craftsmenlabs.gareth2.model

import java.io.Serializable
import java.time.LocalDateTime

data class Experiment(
        val details: ExperimentDetails,
        val timing: ExperimentTiming,
        val results: ExperimentResults,
        val id: String,
        val environment: Map<String, Any> = mapOf()) : Serializable {

    fun getState(): ExperimentState {
        if (timing.ready == null) {
            return ExperimentState.NEW
        } else if (timing.started == null) {
            return ExperimentState.READY
        } else if (timing.waitingForBaseline == null) {
            return ExperimentState.STARTED
        } else if (timing.baselineExecuted == null) {
            return ExperimentState.WAITING_FOR_BASELINE
        } else if (timing.waitingForAssume == null) {
            return ExperimentState.BASELINE_EXECUTED
        } else if (timing.assumeExecuted == null) {
            return ExperimentState.WAITING_FOR_ASSUME
        } else if (timing.waitingFinalizing == null) {
            return ExperimentState.ASSUME_EXECUTED
        } else if (timing.finalizingExecuted == null) {
            return ExperimentState.WAITING_FOR_FINALISATION
        } else if (timing.completed == null) {
            return ExperimentState.FINALISATION_EXECUTED
        } else {
            return ExperimentState.COMPLETED
        }
    }
}

data class ExperimentDetails (
        val name: String,
        val baseline: String,
        val assume: String,
        val time: String,
        val success: String,
        val failure: String,
        val value: Int
)

data class ExperimentTiming(
        val created: LocalDateTime,
        var ready: LocalDateTime? = null,
        var started: LocalDateTime? = null,
        var waitingForBaseline: LocalDateTime? = null,
        var baselineExecuted: LocalDateTime? = null,
        var waitingForAssume: LocalDateTime? = null,
        var assumeExecuted: LocalDateTime? = null,
        var waitingFinalizing: LocalDateTime? = null,
        var finalizingExecuted: LocalDateTime? = null,
        var completed: LocalDateTime? = null
)

data class ExperimentResults(
        var success: Boolean? = null
)

enum class ExperimentState {
    NEW, READY, STARTED, WAITING_FOR_BASELINE, BASELINE_EXECUTED,
    WAITING_FOR_ASSUME, ASSUME_EXECUTED, WAITING_FOR_FINALISATION, FINALISATION_EXECUTED, COMPLETED
}
