package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.definitions.InvokableMethod
import org.craftsmenlabs.gareth.execution.dto.DefinitionInfo
import org.craftsmenlabs.gareth.execution.dto.DurationDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class DefinitionInfoService
@Autowired constructor(val definitionService: DefinitionService) {

    fun getDurationByGlueline(glueLine: String): DurationDTO =
            definitionService.getTime(glueLine)

    fun getInfoByType(glueLine: String, type: ExecutionType) = definitionInfo(definitionService.getMethodForType(glueLine, type))

    private fun definitionInfo(method: InvokableMethod) =
            DefinitionInfo(method.getRegexPatternForGlueLine(), method.getMethodName(), method.getClassName())

}


