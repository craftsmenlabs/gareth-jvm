package org.craftsmenlabs.gareth.validator.client.rest

import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
/**
 *This is a temporary solution. For now there is only ever one execution server for every project, semi-hardcoded in an application property.
 */
class StaticServiceDiscovery : ExecutionServiceDiscovery {

    @Value("\${execution.url}")
    private lateinit var executionURL: String

    override fun createUrl(projectId: String, affix: String): String {
        return "$executionURL/gareth/validator/v1/$affix"
    }
}