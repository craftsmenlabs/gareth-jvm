package org.craftsmenlabs.gareth.model

import java.time.LocalDateTime

data class ExperimentDTO(val id: Long,
                         val name: String,
                         val value: Int = 0,
                         override val glueLines: Gluelines,
                         val created: LocalDateTime,
                         val ready: LocalDateTime? = null,
                         val due: LocalDateTime? = null,
                         val baselineExecuted: LocalDateTime? = null,
                         val completed: LocalDateTime? = null,
                         val result: ExecutionStatus = ExecutionStatus.PENDING,
                         val environment: ExperimentRunEnvironment) : HasGlueLines

data class ExperimentTemplateDTO(
        val id: Long,
        val created: LocalDateTime,
        val ready: LocalDateTime?,
        val name: String,
        val value: Int = 0,
        override val glueLines: Gluelines
) : HasGlueLines

data class ExperimentCreateDTO(val templateId: Long, val dueDate: LocalDateTime? = null)

data class ExperimentTemplateUpdateDTO(
        val id: Long,
        val name: String? = null,
        val value: Int? = null,
        val baseline: String? = null,
        val assume: String? = null,
        val success: String? = null,
        val failure: String? = null,
        val time: String? = null)

data class ExperimentTemplateCreateDTO(
        val name: String,
        val value: Int = 0,
        override val glueLines: Gluelines) : HasGlueLines

data class OverviewDTO(val name: String,
                       val templateId: Long,
                       val ready: Boolean = false,
                       val editable: Boolean = true,
                       val lastRun: LocalDateTime? = null,
                       val nextRun: LocalDateTime? = null,
                       val pending: Int = 0,
                       val running: Int = 0,
                       val success: Int = 0,
                       val failed: Int = 0
)