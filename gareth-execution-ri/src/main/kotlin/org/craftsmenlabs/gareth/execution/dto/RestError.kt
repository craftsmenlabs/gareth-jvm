package org.craftsmenlabs.gareth.execution.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


data class RestError @JsonCreator constructor(@JsonProperty(value = "status", required = true) var status: Int,
                                              @JsonProperty(value = "message", required = true) var message: String)