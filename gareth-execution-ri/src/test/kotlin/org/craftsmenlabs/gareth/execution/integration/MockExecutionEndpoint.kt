package org.craftsmenlabs.gareth.execution.integration

import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.rest.ExecutionEndpointClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/execution")
class MockExecutionEndpoint : ExecutionEndpointClient {

    val baselineCache = mutableMapOf<String, ExecutionRequest>()
    val assumptionCache = mutableMapOf<String, ExecutionRequest>()
    val assumptionExecutionResults = mutableMapOf<String, AssumeExecutionResult>()

    fun addExperimentToExecute(id: String, gluelines: ValidatedGluelines) {
        baselineCache[id] = ExecutionRequest(id, RunContext(), gluelines)
    }
    @RequestMapping(value = "/baselinestatus/{id}", method = arrayOf(RequestMethod.PUT))

    override fun updateBaselineStatus(result: BaselineExecutionResult) {
        val cached = baselineCache[result.experimentId]!!
        if (result.success)
            assumptionCache[result.experimentId] = ExecutionRequest(result.experimentId, result.runContext, cached.glueLines)
        baselineCache.remove(result.experimentId)
    }

    override fun updateAssumeStatus(result: AssumeExecutionResult) {
        assumptionExecutionResults[result.experimentId] = result
    }

    @RequestMapping(value = "/baselines/{id}", method = arrayOf(RequestMethod.GET))
    override fun getBaselinesToExecute(@PathVariable("id") id: String): List<ExecutionRequest> {
        return baselineCache.values.toList()
    }

    @RequestMapping(value = "/assumes/{id}", method = arrayOf(RequestMethod.GET))
    override fun getAssumesToExecute(@PathVariable("id") id: String): List<ExecutionRequest> {
        return assumptionCache.values.toList()
    }

}