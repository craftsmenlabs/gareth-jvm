package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.craftsmenlabs.gareth.validator.ExecutionResource
import org.craftsmenlabs.gareth.validator.model.Duration
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.model.ExecutionResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/")
@Api("Endpoint for experiment execution")
class ExecutionEndPoint @Autowired constructor(val definitionService: DefinitionService) : ExecutionResource {

    @ApiOperation("Establishes the baseline values for an experiment")
    @RequestMapping(value = "baseline", method = arrayOf(RequestMethod.PUT))
    override fun executeBaseline(@ApiParam("") @RequestBody dto: ExecutionRequest): ExecutionResult = definitionService.executeBaseline(dto)

    @ApiOperation("After the time interval has passed as specified by the call to the /duration endpoint, this call establishes whether the assumption passes or fails.")
    @RequestMapping(value = "assume", method = arrayOf(RequestMethod.PUT))
    override fun executeAssumption(@RequestBody dto: ExecutionRequest): ExecutionResult {
        return definitionService.executeAssumption(dto)
    }

    @ApiOperation("Resolves the number of milliseconds")
    @RequestMapping(value = "time", method = arrayOf(RequestMethod.PUT))
    override fun getTime(@RequestBody dto: ExecutionRequest): Duration {
        return definitionService.getTime(dto.glueLines.time)
    }

}

