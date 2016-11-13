package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.dto.DurationDTO
import org.craftsmenlabs.gareth.execution.dto.ExecutionRequestDTO
import org.craftsmenlabs.gareth.execution.services.DefinitionInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
@Api("Endpoint for definitions")
class DefinitionsEndpoint @Autowired constructor(val definitionService: DefinitionInfoService) {

    @RequestMapping(value = "definitions/baseline", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Gets the result of the match for the user and the given perspective")
    fun getBaselineByGlueline(@RequestBody dto: ExecutionRequestDTO) =
            definitionService.getInfoByType(dto, ExecutionType.BASELINE)

    @RequestMapping(value = "definitions/assume", method = arrayOf(RequestMethod.GET))
    fun getAssumeByGlueline(@RequestBody dto: ExecutionRequestDTO) =
            definitionService.getInfoByType(dto, ExecutionType.ASSUME)

    @RequestMapping(value = "definitions/success", method = arrayOf(RequestMethod.GET))
    fun getSuccessByGlueline(@RequestBody dto: ExecutionRequestDTO) =
            definitionService.getInfoByType(dto, ExecutionType.SUCCESS)

    @RequestMapping(value = "definitions/failure", method = arrayOf(RequestMethod.GET))
    fun getFailureByGlueline(@RequestBody dto: ExecutionRequestDTO) =
            definitionService.getInfoByType(dto, ExecutionType.FAILURE)

    @RequestMapping(value = "definitions/time", method = arrayOf(RequestMethod.GET))
    fun getDurationByGlueline(@RequestBody dto: ExecutionRequestDTO): DurationDTO =
            definitionService.getDurationByGlueline(dto.glueline)

}

