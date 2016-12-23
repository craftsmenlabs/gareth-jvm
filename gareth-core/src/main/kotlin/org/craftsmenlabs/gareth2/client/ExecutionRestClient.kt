package org.craftsmenlabs.gareth2.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ExecutionRestClient {
    @Value("\${execution.client.url}")
    lateinit var url: String

}