package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.api.execution.ExecutionRequest
import org.craftsmenlabs.gareth.api.execution.ExecutionResult
import org.craftsmenlabs.gareth.api.model.DefinitionInfo
import org.craftsmenlabs.gareth.api.model.Duration
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ExecutionRestClient {
    @Value("\${execution.client.url}")
    lateinit var host: String

    val restClient = BasicAuthenticationRestClient()

    fun executeLifeCycleStage(type: GlueLineType, executionRequest: ExecutionRequest): ExecutionResult {
        //one of assume, baseline, failure, success
        val fullUrl = createUrl(type.name.toLowerCase())
        val response: ResponseEntity<ExecutionResult> = restClient.putAsEntity(executionRequest, ExecutionResult::class.java, fullUrl)
        return response.body
    }

    fun getDuration(executionRequest: ExecutionRequest): Duration {
        val response: ResponseEntity<Duration> = restClient.putAsEntity(executionRequest, Duration::class.java, createUrl("time"))
        return response.body
    }

    fun isValidGlueLine(type: String, content: String): Boolean {
        val fullUrl = createUrl("definitions/$type/$content")
        val entity = restClient.getAsEntity(DefinitionInfo::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            return false;
        }
        val info = entity.body
        return info.glueline == content
    }


    fun createUrl(affix: String) = "$host/gareth/v1/$affix"
}