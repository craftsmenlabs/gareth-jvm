package org.craftsmenlabs.gareth.validator.integration

import org.craftsmenlabs.gareth.validator.client.ExecutionServiceDiscovery
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MockServiceDiscovery : ExecutionServiceDiscovery {

    override fun createUrl(projectId: String, affix: String): String {
        return "http://localhost:8100/gareth/validator/v1/$affix"
    }
}