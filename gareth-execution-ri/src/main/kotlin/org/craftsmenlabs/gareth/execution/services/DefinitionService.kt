package org.craftsmenlabs.gareth.execution.services

import com.google.common.reflect.ClassPath
import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.*
import org.craftsmenlabs.gareth.execution.dto.DurationDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
open class DefinitionService @Autowired constructor(definitionFactory: DefinitionFactory) {

    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)
    val factory = ParsedDefinitionFactory(definitionFactory)
    val definitionRegistry = DefinitionRegistry(definitionFactory)

    @PostConstruct
    fun init() {
        val classes = getClassesInPackage("org.craftsmenlabs.gareth.execution.spi")
        classes.forEach { clz ->
            log.info("Parsing class ${clz.name}")
            definitionRegistry.addParsedDefinition(factory.parse(clz))
            log.info("Successfully parsed ${clz.simpleName}")
        }
    }

    fun getMethodForType(glueline: String, type: ExecutionType): InvokableMethod =
            definitionRegistry.getMethodDescriptorForExecutionType(glueline, type)

    fun executeByType(glueline: String, type: ExecutionType, request: ExecutionRequestDTO): RunContext =
            definitionRegistry.invokeMethodByType(glueline, type, request)

    fun getDuration(glueline: String): DurationDTO = DurationDTO.createForMinutes(definitionRegistry.getDurationForGlueline(glueline))

    private fun getClassesInPackage(packageName: String): List<Class<*>> {
        val classesInfo = ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(packageName)
        return classesInfo.map { Class.forName(it.name) }
    }


}



