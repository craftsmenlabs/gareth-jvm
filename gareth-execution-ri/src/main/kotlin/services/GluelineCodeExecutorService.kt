package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException
import org.craftsmenlabs.gareth.execution.dto.GlueLineDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GluelineCodeExecutorService @Autowired constructor(val gluelineCodeRegistry: GluelineCodeRegistry) {

    fun executeBaseline(dto: GlueLineDTO) {
        gluelineCodeRegistry.executeBaseline(dto.glueline)
    }

    fun executeAssumption(dto: GlueLineDTO): Boolean {
        try {
            gluelineCodeRegistry.executeAssumption(dto.glueline)
            return true;
        } catch(garethException: GarethUnknownDefinitionException) {
            throw garethException;
        } catch(e: Exception) {
            return false
        }
    }

    fun executeSuccess(dto: GlueLineDTO) {
        gluelineCodeRegistry.executeSuccess(dto.glueline)
    }

    fun executeFailure(dto: GlueLineDTO) {
        gluelineCodeRegistry.executeFailure(dto.glueline)
    }

    fun getDurationInMillis(dto: GlueLineDTO) =
            gluelineCodeRegistry.getDurationInMillis(dto.glueline)


}


