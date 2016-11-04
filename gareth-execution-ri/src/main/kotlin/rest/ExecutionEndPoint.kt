package org.craftsmenlabs.gareth.execution.rest

import org.craftsmenlabs.gareth.execution.dto.GlueLineDTO
import org.craftsmenlabs.gareth.execution.services.GluelineCodeExecutorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class ExecutionEndPoint @Autowired constructor(val gluelineCodeExecutionService: GluelineCodeExecutorService) {

    @RequestMapping(value = "/baseline", method = arrayOf(RequestMethod.PUT))
    fun executeBaseline(@RequestBody dto: GlueLineDTO): Unit {
        gluelineCodeExecutionService.executeBaseline(dto)
    }

    @RequestMapping(value = "/assumption", method = arrayOf(RequestMethod.PUT))
    fun executeAssumption(@RequestBody dto: GlueLineDTO): Boolean {
        return gluelineCodeExecutionService.executeAssumption(dto)
    }

    @RequestMapping(value = "/success", method = arrayOf(RequestMethod.PUT))
    fun executeSuccess(@RequestBody dto: GlueLineDTO) {
        return gluelineCodeExecutionService.executeSuccess(dto)
    }

    @RequestMapping(value = "/failure", method = arrayOf(RequestMethod.PUT))
    fun executeFailure(@RequestBody dto: GlueLineDTO) {
        return gluelineCodeExecutionService.executeFailure(dto)
    }

    @RequestMapping(value = "/duration", method = arrayOf(RequestMethod.PUT))
    fun getDuration(@RequestBody dto: GlueLineDTO): Long {
        return gluelineCodeExecutionService.getDurationInMillis(dto)
    }

}

