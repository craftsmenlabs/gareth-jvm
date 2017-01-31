package org.craftsmenlabs.gareth.model

import java.util.*

data class ExperimentDTO(val id: String,
                         val name: String,
                         val weight: Int = 0,
                         val baseline: String,
                         val assume: String,
                         val success: String,
                         val failure: String,
                         val time: String,
                         val created: Date,
                         val ready: Date? = null,
                         val started: Date? = null,
                         val baselineExecuted: Date? = null,
                         val completed: Date? = null,
                         val result: Boolean = false,
                         val environment: ExperimentRunEnvironment)

data class ExperimentCreateDTO(
        val name: String,
        val weight: Int = 0,
        val baseline: String,
        val assume: String,
        val success: String,
        val failure: String,
        val time: String,
        val environment: ExperimentRunEnvironment)


