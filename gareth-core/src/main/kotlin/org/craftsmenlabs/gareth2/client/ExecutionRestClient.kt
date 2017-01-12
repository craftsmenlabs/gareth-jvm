package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.api.model.DefinitionInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ExecutionRestClient {
    @Value("\${execution.client.url}")
    lateinit var host: String

    lateinit var template: RestTemplate

    fun isValidGlueLine(type: String, content: String): Boolean {
        val fullUrl = getUrl() + "definitions/$type/$content"
        val entity = template.getForEntity(fullUrl, DefinitionInfo::class.java)
        if (!entity.statusCode.is2xxSuccessful) {
            return false;
        }
        val info = entity.body
        return info.glueline == content
    }

    fun getUrl() = "$host/gareth/v1/"

}