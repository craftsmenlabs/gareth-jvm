package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.GarethInvocationException
import org.craftsmenlabs.gareth.validator.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class ExecutionService @Autowired constructor(private val definitionFactory: DefinitionFactory) {

    fun executeBaseline(request: ExecutionRequest): BaselineExecutionResult {
        val invocationResult = invokeVoidMethodByType(request.glueLines.baseline, GlueLineType.BASELINE, request)
        val success = invocationResult.exception == null
        val executeAssumptionDate = if (!success) null else definitionFactory.getTimeToExecuteAssumption(request.glueLines.time)
        return BaselineExecutionResult(
                experimentId = request.experimentId,
                runContext = invocationResult.context,
                success = success,
                assumptionDue = executeAssumptionDate)
    }

    fun executeAssumption(dto: ExecutionRequest): AssumeExecutionResult {
        val invocationResult: AssumptionInvocationResult = invokeAssumptionMethod(dto.glueLines.assume, dto)
        if (invocationResult.exception != null)
            return AssumeExecutionResult(experimentId = dto.experimentId, runContext = invocationResult.context, status = ExecutionStatus.ERROR)
        val isSuccess = invocationResult.successful ?: false
        val result = AssumeExecutionResult(experimentId = dto.experimentId, runContext = invocationResult.context, status = ExecutionStatus.RUNNING)
        val copy = dto.copy(runContext = result.runContext)
        if (isSuccess) {
            return executeSuccess(copy)
        } else {
            return executeFailure(copy)
        }
    }

    private fun executeSuccess(dto: ExecutionRequest): AssumeExecutionResult {
        val context = executeOptionalGlueline(dto.glueLines.success, GlueLineType.SUCCESS, dto)
        return AssumeExecutionResult(experimentId = dto.experimentId, runContext = context, status = ExecutionStatus.SUCCESS)
    }

    private fun executeFailure(dto: ExecutionRequest): AssumeExecutionResult {
        val context = executeOptionalGlueline(dto.glueLines.failure, GlueLineType.FAILURE, dto)
        return AssumeExecutionResult(experimentId = dto.experimentId, runContext = context, status = ExecutionStatus.FAILURE)
    }

    private fun executeOptionalGlueline(glueline: String?, type: GlueLineType, request: ExecutionRequest): RunContext {
        //if the glueline is empty, there's nothing to invoke
        return if (glueline == null || glueline.isBlank()) request.runContext
        else
            invokeMethodByType(glueline, type, request.runContext).context
    }

    private fun invokeAssumptionMethod(glueLine: String, request: ExecutionRequest): AssumptionInvocationResult {
        val executionResult: GluelineInvocationResult = invokeMethodByType(glueLine, GlueLineType.ASSUME, request.runContext)
        if (executionResult.result != null) {
            return AssumptionInvocationResult(successful = executionResult.result as Boolean, context = executionResult.context)
        } else {
            return AssumptionInvocationResult(exception = executionResult.exception, context = executionResult.context)
        }
    }

    private fun invokeVoidMethodByType(glueLine: String, type: GlueLineType, request: ExecutionRequest): GluelineInvocationResult {
        return invokeMethodByType(glueLine, type, request.runContext)
    }

    private fun invokeMethodByType(glueLine: String, type: GlueLineType, context: RunContext): GluelineInvocationResult {
        try {
            val invocationResult = definitionFactory.invokeGlueline(glueLine, type, context)
            return GluelineInvocationResult(result = invocationResult, context = context)
        } catch (e: Exception) {
            context.storeString("ERROR_DURING_" + type.name, e.message ?: "")
            return GluelineInvocationResult(context = context, exception = GarethInvocationException(cause = e))
        }
    }

}






