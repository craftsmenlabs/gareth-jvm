package org.craftsmenlabs.gareth.validator.services

import org.craftsmenlabs.gareth.validator.client.ExecutionRestClient
import org.craftsmenlabs.gareth.validator.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GluelineService @Autowired constructor(val executionRestClient: ExecutionRestClient) {

    fun lookupGlueline(glueLine: GlueLineType,
                       content: String): GlueLineSearchResultDTO =
            executionRestClient.lookupGlueline(glueLine, content)
}