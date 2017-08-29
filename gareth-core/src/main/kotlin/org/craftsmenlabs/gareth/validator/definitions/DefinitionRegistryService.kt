package org.craftsmenlabs.gareth.validator.definitions

import org.craftsmenlabs.gareth.validator.model.DefinitionRegistryDTO
import org.springframework.stereotype.Service

@Service
class DefinitionRegistryService {

    val registry = HashMap<String, ClientDefinitionsRegistry>()

    fun registerDefinitionsForClient(client: String, clientRegistry: DefinitionRegistryDTO) {
        registry[client] = ClientDefinitionsRegistry(clientRegistry.glueLinesPerCategory)
    }

    fun getRegistryForClient(project: String): ClientDefinitionsRegistry = registry[project] ?: throw IllegalArgumentException("not a valid project: $project")


}