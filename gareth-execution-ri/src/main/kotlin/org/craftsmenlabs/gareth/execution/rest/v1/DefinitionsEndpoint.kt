package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.craftsmenlabs.gareth.model.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
@Api("Endpoint for definitions")
class DefinitionsEndpoint @Autowired constructor(val definitionService: DefinitionService) {

    @RequestMapping(value = "definitions/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Gets the result of the match for the user and the given perspective")
    fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.BASELINE)

    @RequestMapping(value = "definitions/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.ASSUME)

    @RequestMapping(value = "definitions/success/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.SUCCESS)

    @RequestMapping(value = "definitions/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getFailureByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.FAILURE)

    @RequestMapping(value = "definitions/time/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): Duration =
            definitionService.getTime(glueLine)

}

