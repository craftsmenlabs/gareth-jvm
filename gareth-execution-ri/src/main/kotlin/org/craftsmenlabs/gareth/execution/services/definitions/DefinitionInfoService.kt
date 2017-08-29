package org.craftsmenlabs.gareth.execution.services.definitions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefinitionInfoService @Autowired constructor(val definitionRegistry: DefinitionRegistryService) {

    //fun getInfoByType(glueLine: String, type: ExecutionType): DefinitionInfo = definitionRegistry.getDefinitionInfoForGluelineAndType(glueLine, type)

    //fun getDurationForGlueline(glueline: String): Duration = DurationBuilder.createForMinutes(definitionRegistry.getTimeToExecuteAssumption(glueline).second)

}