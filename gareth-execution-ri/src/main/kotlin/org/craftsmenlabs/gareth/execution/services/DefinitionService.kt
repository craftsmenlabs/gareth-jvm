package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.api.execution.ExecutionRequest
import org.craftsmenlabs.gareth.api.execution.ExecutionResult
import org.craftsmenlabs.gareth.api.execution.ExecutionRunContext
import org.craftsmenlabs.gareth.api.execution.ExecutionStatus
import org.craftsmenlabs.gareth.api.model.DefinitionInfo
import org.craftsmenlabs.gareth.api.model.Duration
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.dto.DurationBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class DefinitionService @Autowired constructor(val definitionRegistry: DefinitionRegistry) {

    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)

    fun executeBaseline(dto: ExecutionRequest): ExecutionResult {
        val context: ExecutionRunContext = executeByType(dto.glueLine, ExecutionType.BASELINE, dto)
        return context.toExecutionResult(ExecutionStatus.RUNNING)
    }

    fun executeAssumption(dto: ExecutionRequest): ExecutionResult {
        val successAndContext = definitionRegistry.invokeAssumptionMethod(dto.glueLine, dto)
        val isSuccess = successAndContext.first
        val context = successAndContext.second
        return if (isSuccess) context.toExecutionResult(ExecutionStatus.SUCCESS) else context.toExecutionResult(ExecutionStatus.FAILURE)
    }

    fun executeSuccess(dto: ExecutionRequest): ExecutionResult {
        val context = executeByType(dto.glueLine, ExecutionType.SUCCESS, dto)
        return context.toExecutionResult(ExecutionStatus.SUCCESS)
    }


    fun executeFailure(dto: ExecutionRequest): ExecutionResult {
        val context = executeByType(dto.glueLine, ExecutionType.FAILURE, dto)
        return context.toExecutionResult(ExecutionStatus.FAILURE)
    }

    fun getInfoByType(glueLine: String, type: ExecutionType): DefinitionInfo {
        val method = definitionRegistry.getMethodDescriptorForExecutionType(glueLine, type)
        return DefinitionInfo(method.getRegexPatternForGlueLine(), method.getMethodName(), method.getClassName())
    }

    fun getTime(glueline: String): Duration = DurationBuilder.createForMinutes(definitionRegistry.getTimeForGlueline(glueline))

    private fun executeByType(glueline: String, type: ExecutionType, request: ExecutionRequest): ExecutionRunContext =
            definitionRegistry.invokeVoidMethodByType(glueline, type, request)

}






