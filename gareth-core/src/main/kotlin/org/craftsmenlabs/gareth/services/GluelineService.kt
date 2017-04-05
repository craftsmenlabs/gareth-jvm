package org.craftsmenlabs.gareth.services

import org.craftsmenlabs.gareth.client.ExecutionRestClient
import org.craftsmenlabs.gareth.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GluelineService @Autowired constructor(val executionRestClient: ExecutionRestClient) {

    fun lookupGlueline(glueLine: GlueLineType,
                       content: String): GlueLineSearchResultDTO =
            executionRestClient.lookupGlueline(glueLine, content)
}