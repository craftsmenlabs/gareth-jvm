package org.craftsmenlabs.gareth.execution.dto

class ExecutionResultDTO() {
    lateinit var status: ExecutionStatus
    lateinit var environment: ExperimentRunEnvironmentDTO

    companion object {
        fun create(status: ExecutionStatus,
                   environment: ExperimentRunEnvironmentDTO
        ): ExecutionResultDTO {
            val dto = ExecutionResultDTO()
            dto.status = status
            dto.environment = environment
            return dto
        }
    }


}

