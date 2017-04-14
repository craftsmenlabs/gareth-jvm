package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.craftsmenlabs.gareth.execution.services.GlueLineMatcherService
import org.craftsmenlabs.gareth.validator.GlueLineMatcherResource
import org.craftsmenlabs.gareth.validator.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/")
@Api("Endpoint for glueline searches", description = "This rest endpoint helps you locate the executable methods for " +
        "glueline types by matching a given string. The object that is returned gives a list of partial matches in the 'suggestions' property, and " +
        "if the given string matches exactly the appropriate regular expression if returned in the 'exact' property.")
class GlueLineMatcherEndpoint @Autowired constructor(val glueLineMatcher: GlueLineMatcherService) : GlueLineMatcherResource {

    @RequestMapping(value = "search/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Returns baseline definition matches for the given (partial) glueline")
    override fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            glueLineMatcher.getMatches(GlueLineType.BASELINE, glueLine)

    @RequestMapping(value = "search/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Returns assumption definition matches for the given (partial) glueline")
    override fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            glueLineMatcher.getMatches(GlueLineType.ASSUME, glueLine)

    @RequestMapping(value = "search/success/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Returns success definition matches for the given (partial) glueline")
    override fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            glueLineMatcher.getMatches(GlueLineType.SUCCESS, glueLine)

    @RequestMapping(value = "search/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Returns failure definition matches for the given (partial) glueline")
    override fun getFailureByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            glueLineMatcher.getMatches(GlueLineType.FAILURE, glueLine)

    @RequestMapping(value = "search/time/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Returns time definition matches for the given (partial) glueline")
    override fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            glueLineMatcher.getMatches(GlueLineType.TIME, glueLine)

}

