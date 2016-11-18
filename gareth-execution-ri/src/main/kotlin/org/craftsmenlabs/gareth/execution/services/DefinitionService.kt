package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.dto.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class DefinitionService @Autowired constructor(val definitionRegistry: DefinitionRegistry) {

    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)

    fun executeBaseline(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context: RunContext = executeByType(dto.glueline, ExecutionType.BASELINE, dto)
        return context.toExecutionResult(ExecutionStatus.RUNNING)
    }

    fun executeAssumption(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val successAndContext = definitionRegistry.invokeAssumptionMethod(dto.glueline, dto)
        val isSuccess = successAndContext.first
        val context = successAndContext.second
        return if (isSuccess) context.toExecutionResult(ExecutionStatus.SUCCESS) else context.toExecutionResult(ExecutionStatus.FAILURE)
    }

    fun executeSuccess(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context = executeByType(dto.glueline, ExecutionType.SUCCESS, dto)
        return context.toExecutionResult(ExecutionStatus.SUCCESS)
    }


    fun executeFailure(dto: ExecutionRequestDTO): ExecutionResultDTO {
        val context = executeByType(dto.glueline, ExecutionType.FAILURE, dto)
        return context.toExecutionResult(ExecutionStatus.FAILURE)
    }

    fun getInfoByType(glueLine: String, type: ExecutionType): DefinitionInfo {
        val method = definitionRegistry.getMethodDescriptorForExecutionType(glueLine, type)
        return DefinitionInfo(method.getRegexPatternForGlueLine(), method.getMethodName(), method.getClassName())
    }

    fun getTime(glueline: String): DurationDTO = DurationDTO.createForMinutes(definitionRegistry.getTimeForGlueline(glueline))

    private fun executeByType(glueline: String, type: ExecutionType, request: ExecutionRequestDTO): RunContext =
            definitionRegistry.invokeVoidMethodByType(glueline, type, request)

}






