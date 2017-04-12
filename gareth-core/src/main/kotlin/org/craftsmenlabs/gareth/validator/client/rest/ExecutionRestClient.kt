package org.craftsmenlabs.gareth.validator.client.rest

import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.craftsmenlabs.gareth.validator.client.GlueLineExecutor
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.rest.BasicAuthenticationRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

@Service
class ExecutionRestClient @Autowired constructor(val serviceDiscovery: ExecutionServiceDiscovery) : GlueLineExecutor {


    lateinit var restClient: BasicAuthenticationRestClient
    val log = LoggerFactory.getLogger(ExecutionRestClient::class.java)

    @PostConstruct
    fun init() {
        restClient = BasicAuthenticationRestClient()
    }

    override fun executeBaseline(experiment: ExperimentDTO): ExecutionResult {
        return executeLifeCycleStage(experiment.projectId, GlueLineType.BASELINE, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun executeAssume(experiment: ExperimentDTO): ExecutionResult {
        return executeLifeCycleStage(experiment.projectId, GlueLineType.ASSUME, ExecutionRequest(experiment.environment, experiment.glueLines))
    }

    override fun getDuration(experiment: ExperimentDTO): java.time.Duration {
        val duration = getDuration(experiment.projectId, ExecutionRequest(experiment.environment, experiment.glueLines))
        return java.time.Duration.of(duration.amount, ChronoUnit.valueOf(duration.unit))
    }


    private fun executeLifeCycleStage(projectId: String, type: GlueLineType, executionRequest: ExecutionRequest): ExecutionResult {
        //one of assume, baseline, failure, success
        val fullUrl = serviceDiscovery.createUrl(projectId, type.name.toLowerCase())
        log.debug("Executing lifecycle stage {}", type.name)
        val response: ResponseEntity<ExecutionResult> = restClient.putAsEntity(executionRequest, ExecutionResult::class.java, fullUrl)
        if (!response.statusCode.is2xxSuccessful) {
            log.error("Error executing glueline $type for experiment")
            return ExecutionResult(status = ExecutionStatus.ERROR, environment = ExperimentRunEnvironment())
        } else
            return response.body
    }

    private fun getDuration(projectId: String, executionRequest: ExecutionRequest): Duration {
        log.debug("Getting duration for {}", executionRequest.glueLines.time)
        val response: ResponseEntity<Duration> = restClient.putAsEntity(executionRequest, Duration::class.java, serviceDiscovery.createUrl(projectId, "time"))
        return response.body
    }

}