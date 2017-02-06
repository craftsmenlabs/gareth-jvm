package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ExecutionRestClient constructor(@Value("\${execution.client.url}") val host: String,
                                      @Value("\${execution.client.user}") val user: String,
                                      @Value("\${execution.client.password}") val password: String) {

    lateinit var restClient: BasicAuthenticationRestClient
    val log = LoggerFactory.getLogger(ExecutionRestClient::class.java)

    @PostConstruct
    fun init() {
        restClient = BasicAuthenticationRestClient(user, password)
    }

    fun executeLifeCycleStage(type: GlueLineType, executionRequest: ExecutionRequest): ExecutionResult {
        //one of assume, baseline, failure, success
        val fullUrl = createUrl(type.name.toLowerCase())
        log.debug("Executing lifecycle stage {}", type.name)
        val response: ResponseEntity<ExecutionResult> = restClient.putAsEntity(executionRequest, ExecutionResult::class.java, fullUrl)
        return response.body
    }

    fun getDuration(executionRequest: ExecutionRequest): Duration {
        log.debug("Getting duration for {}", executionRequest.glueLine)
        val response: ResponseEntity<Duration> = restClient.putAsEntity(executionRequest, Duration::class.java, createUrl("time"))
        return response.body
    }

    fun isValidGlueLine(type: String, content: String): Boolean {
        val fullUrl = createUrl("definitions/$type/$content")
        val entity = restClient.getAsEntity(DefinitionInfo::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            log.warn("Glueline '{}' of type {} is not a valid glue line", content, type)
            return false;
        }
        log.debug("'{}' is a valid {} glueline", content, type)
        val info = entity.body
        return info.glueline != null
    }

    fun isValidTimeGlueLine(content: String): Boolean {
        val fullUrl = createUrl("definitions/time/$content")
        val entity = restClient.getAsEntity(Duration::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            log.warn("Glueline '{}' is not a valid time glueline", content)
            return false;
        }
        log.warn("Glueline '{}' is a valid time glueline", content)
        return entity.body != null
    }


    fun createUrl(affix: String) = "$host/gareth/v1/$affix"
}