package org.craftsmenlabs.gareth.model

import java.time.LocalDateTime

data class ExperimentDTO(val id: Long,
                         val name: String,
                         val value: Int = 0,
                         val baseline: String,
                         val assume: String,
                         val success: String,
                         val failure: String,
                         val time: String,
                         val created: LocalDateTime,
                         val ready: LocalDateTime? = null,
                         val started: LocalDateTime? = null,
                         val baselineExecuted: LocalDateTime? = null,
                         val completed: LocalDateTime? = null,
                         val result: Boolean = false,
                         val environment: ExperimentRunEnvironment)

data class ExperimentCreateDTO(
        val name: String,
        val value: Int = 0,
        val baseline: String,
        val assume: String,
        val success: String,
        val failure: String,
        val time: String,
        val environment: ExperimentRunEnvironment = ExperimentRunEnvironment())
