package org.craftsmenlabs.gareth.model

import java.time.LocalDateTime

data class Experiment(
        val id: String,
        val value: Int = 0,
        val name: String,
        override val glueLines: Gluelines,
        val timing: ExperimentTiming,
        val status: ExecutionStatus = ExecutionStatus.PENDING,
        val environment: ExperimentRunEnvironment = ExperimentRunEnvironment()) : HasGlueLines {

    fun getState(): ExperimentState {
        if (timing.baselineExecuted == null) {
            return ExperimentState.NEW
        } else if (timing.completed == null) {
            return ExperimentState.BASELINE_EXECUTED
        } else {
            return ExperimentState.COMPLETED
        }
    }

    companion object {
        fun createDefault(): Experiment {
            return Experiment(
                    id = "",
                    name = "TEST",
                    value = 1,
                    glueLines = Gluelines(baseline = "baseline", assume = "assume", time = "5 seonds", success = "success", failure = "failure"),
                    timing = ExperimentTiming(),
                    environment = ExperimentRunEnvironment())
        }
    }
}

data class ExperimentTiming(
        val created: LocalDateTime = LocalDateTime.now(),
        val due: LocalDateTime = LocalDateTime.now(),
        val baselineExecuted: LocalDateTime? = null,
        val completed: LocalDateTime? = null
)

enum class ExperimentState {
    NEW,
    BASELINE_EXECUTED,
    COMPLETED
}
