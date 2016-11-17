package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionResultDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefinitionExecutorService @Autowired constructor(val definitionService: DefinitionService) {

    fun executeBaseline(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context: RunContext = definitionService.executeByType(dto.glueline, ExecutionType.BASELINE, dto)
        return context.toExecutionResult(ExecutionStatus.RUNNING)
    }

    fun executeAssumption(dto: ExecutionRequestDTO): ExecutionResultDTO {
        try {
            val context = definitionService.executeByType(dto.glueline, ExecutionType.ASSUME, dto)
            return context.toExecutionResult(ExecutionStatus.SUCCESS)
        } catch(e: Exception) {
            return ExecutionResultDTO.create(ExecutionStatus.FAILURE, dto.environment)
        }
    }

    fun executeSuccess(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context = definitionService.executeByType(dto.glueline, ExecutionType.SUCCESS, dto)
        return context.toExecutionResult(ExecutionStatus.SUCCESS)
    }


    fun executeFailure(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context = definitionService.executeByType(dto.glueline, ExecutionType.FAILURE, dto)
        return context.toExecutionResult(ExecutionStatus.FAILURE)
    }


    fun getTime(dto: ExecutionRequestDTO) =
            definitionService.getTime(dto.glueline)


}


