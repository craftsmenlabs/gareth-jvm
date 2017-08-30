package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.validator.model.DefinitionRegistryDTO
import org.craftsmenlabs.gareth.validator.rest.GarethHubClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefinitionRegistryManager @Autowired constructor(private val endpointClient: GarethHubClient) {

    fun sendRegistryToGarethHub(registry: DefinitionRegistryDTO) {
        endpointClient.updateRegistryForClient(registry = registry)
    }


}