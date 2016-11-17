package org.craftsmenlabs.gareth.execution.dto

data class ExecutionResultDTO(val status: ExecutionStatus,
                              val environment: ExperimentRunEnvironmentDTO)

