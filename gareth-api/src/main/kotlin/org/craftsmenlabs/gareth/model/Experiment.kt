package org.craftsmenlabs.gareth.model

import java.time.LocalDateTime

data class Experiment(
        val details: ExperimentDetails,
        val timing: ExperimentTiming,
        val results: ExperimentResults,
        val id: Long? = null,
        val environment: ExperimentRunEnvironment = ExperimentRunEnvironment()) {

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

    companion object {
        fun createDefault(): Experiment {
            return Experiment(details = ExperimentDetails("TEST", "baseline", "assume", "5 seonds", "success", "failure", 1),
                    timing = ExperimentTiming(),
                    results = ExperimentResults(),
                    environment = ExperimentRunEnvironment())
        }
    }
}

data class ExperimentDetails(
        val name: String,
        val baseline: String,
        val assume: String,
        val time: String,
        val success: String,
        val failure: String,
        val value: Int
)

data class ExperimentTiming(
        val created: LocalDateTime = LocalDateTime.now(),
        val ready: LocalDateTime? = null,
        val started: LocalDateTime? = null,
        val waitingForBaseline: LocalDateTime? = null,
        val baselineExecuted: LocalDateTime? = null,
        val waitingForAssume: LocalDateTime? = null,
        val assumeExecuted: LocalDateTime? = null,
        val waitingFinalizing: LocalDateTime? = null,
        val finalizingExecuted: LocalDateTime? = null,
        val completed: LocalDateTime? = null
)

data class ExperimentResults(val status: ExecutionStatus = ExecutionStatus.RUNNING
)

enum class ExperimentState {
    NEW, READY, STARTED, WAITING_FOR_BASELINE, BASELINE_EXECUTED,
    WAITING_FOR_ASSUME, ASSUME_EXECUTED, WAITING_FOR_FINALISATION, FINALISATION_EXECUTED, COMPLETED
}
