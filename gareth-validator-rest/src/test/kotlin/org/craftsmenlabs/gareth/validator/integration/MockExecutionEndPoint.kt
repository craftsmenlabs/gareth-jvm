package org.craftsmenlabs.integration

import org.craftsmenlabs.gareth.model.Duration
import org.craftsmenlabs.gareth.model.ExecutionRequest
import org.craftsmenlabs.gareth.model.ExecutionResult
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/")
class MockExecutionEndPoint {

    @RequestMapping(value = "baseline", method = arrayOf(RequestMethod.PUT))
    fun executeBaseline(@RequestBody dto: ExecutionRequest): ExecutionResult = ExecutionResult(dto.environment, ExecutionStatus.RUNNING)

    @RequestMapping(value = "assume", method = arrayOf(RequestMethod.PUT))
    fun executeAssumption(@RequestBody dto: ExecutionRequest): ExecutionResult = ExecutionResult(dto.environment, ExecutionStatus.SUCCESS)

    @RequestMapping(value = "time", method = arrayOf(RequestMethod.PUT))
    fun getTime(@RequestBody dto: ExecutionRequest): Duration = Duration("SECONDS", 2)

}

