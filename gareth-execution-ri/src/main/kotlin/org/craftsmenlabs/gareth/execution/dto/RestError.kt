package org.craftsmenlabs.gareth.execution.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RestError @JsonCreator constructor(@JsonProperty(value = "status", required = true) val status: Int,
                                              @JsonProperty(value = "message", required = true) val message: String)