package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.craftsmenlabs.gareth.execution.dto.DurationDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionResultDTO
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
@Api("Endpoint for experiment execution")
class ExecutionEndPoint @Autowired constructor(val definitionService: DefinitionService) {

    @ApiOperation("Establishes the baseline values for an experiment")
    @RequestMapping(value = "baseline", method = arrayOf(RequestMethod.PUT))
    fun executeBaseline(@ApiParam("") @RequestBody dto: ExecutionRequestDTO): ExecutionResultDTO = definitionService.executeBaseline(dto)

    @ApiOperation("After the time interval has passed as specified by the call to the /duration endpoint, this call establishes whether the assumption passes or fails.")
    @RequestMapping(value = "assume", method = arrayOf(RequestMethod.PUT))
    fun executeAssumption(@RequestBody dto: ExecutionRequestDTO): ExecutionResultDTO {
        return definitionService.executeAssumption(dto)
    }

    @ApiOperation("This call is invoked when the assumption has step evaluated to success")
    @RequestMapping(value = "success", method = arrayOf(RequestMethod.PUT))
    fun executeSuccess(@RequestBody dto: ExecutionRequestDTO): ExecutionResultDTO {
        return definitionService.executeSuccess(dto)
    }

    @ApiOperation("This call is invoked when the assumption has step evaluated to failure")
    @RequestMapping(value = "failure", method = arrayOf(RequestMethod.PUT))
    fun executeFailure(@RequestBody dto: ExecutionRequestDTO): ExecutionResultDTO {
        return definitionService.executeFailure(dto)
    }

    @ApiOperation("Resolves the number of milliseconds")
    @RequestMapping(value = "time", method = arrayOf(RequestMethod.PUT))
    fun getTime(@RequestBody dto: ExecutionRequestDTO): DurationDTO {
        return definitionService.getTime(dto.glueline)
    }

}

