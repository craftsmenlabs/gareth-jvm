package org.craftsmenlabs.gareth.validator.model

import java.time.LocalDateTime

data class ExperimentDTO(val id: String,
                         val name: String,
                         val projectId: String = "",
                         val value: Int = 0,
                         val glueLines: ValidatedGluelines,
                         val created: LocalDateTime,
                         val ready: LocalDateTime? = null,
                         val due: LocalDateTime,
                         val baselineExecuted: LocalDateTime? = null,
                         val completed: LocalDateTime? = null,
                         val status: ExecutionStatus = ExecutionStatus.PENDING,
                         val environment: ExperimentRunEnvironment = ExperimentRunEnvironment()) {

    fun getLifecycleStage(): ExperimentLifecycle {
        if (baselineExecuted == null) {
            return ExperimentLifecycle.NEW
        } else if (completed == null) {
            return ExperimentLifecycle.BASELINE_EXECUTED
        } else {
            return ExperimentLifecycle.COMPLETED
        }
    }

    companion object {
        fun createDefault(now: LocalDateTime): ExperimentDTO {
            return ExperimentDTO(
                    id = "",
                    name = "TEST",
                    value = 1,
                    created = now,
                    due = now,
                    glueLines = ValidatedGluelines(baseline = "baseline", assume = "assume", time = "5 seonds", success = "success", failure = "failure"),
                    environment = ExperimentRunEnvironment())
        }
    }
}

data class ExperimentTemplateDTO(
        val id: String,
        val created: LocalDateTime,
        val ready: LocalDateTime?,
        val name: String,
        val value: Int = 0,
        val glueLines: Gluelines
)

data class ExperimentCreateDTO(val templateId: String, val dueDate: LocalDateTime? = null)

data class ExperimentTemplateUpdateDTO(
        val id: String,
        val name: String? = null,
        val value: Int? = null,
        val baseline: String? = null,
        val assume: String? = null,
        val success: String? = null,
        val failure: String? = null,
        val time: String? = null)

data class ExperimentTemplateCreateDTO(
        val name: String,
        val projectid: String,
        val value: Int = 0,
        val glueLines: Gluelines)

data class OverviewDTO(val name: String,
                       val templateId: String,
                       val ready: Boolean = false,
                       val editable: Boolean = true,
                       val lastRun: LocalDateTime? = null,
                       val nextRun: LocalDateTime? = null,
                       val pending: Int = 0,
                       val running: Int = 0,
                       val success: Int = 0,
                       val failed: Int = 0
)