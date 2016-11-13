package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.execution.dto.DefinitionInfo
import org.craftsmenlabs.gareth.execution.dto.DurationDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class DefinitionInfoService
@Autowired constructor(val definitionService: DefinitionService) {

    fun getDurationByGlueline(glueLine: String): DurationDTO =
            definitionService.getDuration(glueLine)

    fun getInfoByType(dto: ExecutionRequestDTO, type: ExecutionType) = definitionInfo(definitionService.getMethodForType(dto.glueline, type))

    private fun definitionInfo(method: InvokableMethod) =
            DefinitionInfo(method.getRegexPatternForGlueLine(), method.getMethodName(), method.getClassName())

}


