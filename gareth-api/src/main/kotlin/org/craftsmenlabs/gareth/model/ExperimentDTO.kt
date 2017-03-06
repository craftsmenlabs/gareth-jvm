package org.craftsmenlabs.gareth.model

import java.time.LocalDateTime

data class ExperimentDTO(val id: Long,
                         val name: String,
                         val value: Int = 0,
                         override val glueLines: Gluelines,
                         val created: LocalDateTime,
                         val ready: LocalDateTime? = null,
                         val started: LocalDateTime? = null,
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

data class ExperimentCreateDTO(val templateId: Long, val startDate: LocalDateTime? = null)

data class ExperimentTemplateUpdateDTO(
        val id: Long,
        val ready: LocalDateTime? = null,
        val name: String? = null,
        val value: Int? = null,
        val baseline: String? = null,
        val assume: String? = null,
        val success: String? = null,
        val failure: String? = null,
        val time: String? = null) {
    fun gluelinesHaveChanged() = !getChangedGluelines().isEmpty()

    fun getChangedGluelines(): Map<GlueLineType, String> {
        val changed = mutableMapOf<GlueLineType, String>()
        if (baseline != null)
            changed.put(GlueLineType.BASELINE, baseline)
        if (assume != null)
            changed.put(GlueLineType.ASSUME, assume)
        if (success != null)
            changed.put(GlueLineType.SUCCESS, success)
        if (failure != null)
            changed.put(GlueLineType.FAILURE, failure)
        if (time != null)
            changed.put(GlueLineType.TIME, time)
        return changed
    }
}

data class ExperimentTemplateCreateDTO(
        val name: String,
        val value: Int = 0,
        override val glueLines: Gluelines) : HasGlueLines
