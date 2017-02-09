package org.craftsmenlabs.gareth.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class ExperimentDTO(val id: Long,
                         val name: String,
                         val weight: Int = 0,
                         val baseline: String,
                         val assume: String,
                         val success: String,
                         val failure: String,
                         val time: String,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
                         val created: Date,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
                         val ready: Date? = null,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
                         val started: Date? = null,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
                         val baselineExecuted: Date? = null,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
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


