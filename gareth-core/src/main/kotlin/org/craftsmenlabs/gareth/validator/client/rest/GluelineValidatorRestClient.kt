package org.craftsmenlabs.gareth.validator.client.rest

import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.craftsmenlabs.gareth.validator.client.GluelineValidator
import org.craftsmenlabs.gareth.validator.model.*
import org.craftsmenlabs.gareth.validator.rest.BasicAuthenticationRestClient
import org.craftsmenlabs.gareth.validator.time.DurationExpressionParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GluelineValidatorRestClient @Autowired constructor(val serviceDiscovery: ExecutionServiceDiscovery,
                                                         val durationExpressionParser: DurationExpressionParser) : GluelineValidator {

    val log: Logger = LoggerFactory.getLogger(GluelineValidatorRestClient::class.java)

    lateinit var restClient: BasicAuthenticationRestClient

    @PostConstruct
    fun init() {
        restClient = BasicAuthenticationRestClient()
    }

    override fun gluelineIsValid(projectId: String, type: GlueLineType, glueline: String?): Boolean {
        if (glueline == null || glueline.isBlank()) {
            //only success and failure are allowed to be blank
            return type == GlueLineType.SUCCESS || type == GlueLineType.FAILURE
        }
        if (type == GlueLineType.TIME)
            return durationExpressionParser.parse(glueline) != null || isValidTimeGlueLine(projectId, glueline)
        else return isValidGlueLine(projectId, type, glueline)
    }

    override fun validateGluelines(projectId: String, gluelines: Gluelines): Boolean {
        val lines = mapOf<GlueLineType, String?>(
                Pair(GlueLineType.ASSUME, gluelines.assume),
                Pair(GlueLineType.BASELINE, gluelines.baseline),
                Pair(GlueLineType.TIME, gluelines.time),
                Pair(GlueLineType.FAILURE, gluelines.failure),
                Pair(GlueLineType.SUCCESS, gluelines.success))
        return lines.all { gluelineIsValid(projectId, it.key, it.value) }
    }

    /**
     * Connects to the the GlueLineMatcherEndpoint REST controller in execution project
     */
    override fun lookupGlueline(projectId: String, type: GlueLineType, content: String): GlueLineSearchResultDTO {
        val fullUrl = serviceDiscovery.createUrl(projectId, "search/${type.name.toLowerCase()}/$content")
        val entity = restClient.getAsEntity(GlueLineSearchResultDTO::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            log.error("Could not find glueline '{}' of type {}", content, type.name)
            throw IllegalStateException("Error looking up glueline")
        }
        log.debug("'Search results for {}' of type {} {}", content, type.name, entity.body)
        return entity.body
    }

    /**
     * Connects to the the DefinitionsEndPoint REST controller in execution project
     */
    private fun isValidGlueLine(projectId: String, type: GlueLineType, content: String): Boolean {
        val fullUrl = serviceDiscovery.createUrl(projectId, "definitions/${type.name.toLowerCase()}/$content")
        val entity = restClient.getAsEntity(DefinitionInfo::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            log.warn("Glueline '{}' of type {} is not a valid glue line", content, type)
            log.warn("Http status: ${entity.statusCodeValue}")
            return false;
        }
        log.debug("'{}' is a valid {} glueline", content, type.name.toLowerCase())
        val info: DefinitionInfo? = entity.body
        return info?.glueline != null
    }


    private fun isValidTimeGlueLine(projectId: String, content: String): Boolean {
        val fullUrl = serviceDiscovery.createUrl(projectId, "definitions/time/$content")
        val entity = restClient.getAsEntity(Duration::class.java, fullUrl)
        if (!entity.statusCode.is2xxSuccessful) {
            log.warn("Glueline '{}' is not a valid time glueline", content)
            log.warn("Http status: ${entity.statusCodeValue}")
            return false;
        }
        log.warn("Glueline '{}' is a valid time glueline", content)
        return entity.body != null
    }


}