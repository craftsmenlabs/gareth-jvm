package org.craftsmenlabs.gareth.execution.dto

class ExecutionRequestDTO() {
    lateinit var environment: ExperimentRunEnvironmentDTO
    lateinit var glueline: String


    companion object {
        fun create(glueline: String,
                   environment: ExperimentRunEnvironmentDTO = ExperimentRunEnvironmentDTO.createEmpty()
        ): ExecutionRequestDTO {
            val dto = ExecutionRequestDTO()
            dto.glueline = glueline
            dto.environment = environment
            return dto
        }
    }


}

