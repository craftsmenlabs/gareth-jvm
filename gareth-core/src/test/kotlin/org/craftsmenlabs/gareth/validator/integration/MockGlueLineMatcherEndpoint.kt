package org.craftsmenlabs.gareth.validator.integration

import org.craftsmenlabs.gareth.model.GlueLineSearchResultDTO
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/")
class MockGlueLineMatcherEndpoint {

    @RequestMapping(value = "search/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf(glueLine), exact = glueLine)

    @RequestMapping(value = "search/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf(glueLine), exact = glueLine)

    @RequestMapping(value = "search/success/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf(glueLine), exact = glueLine)

    @RequestMapping(value = "search/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getFailureByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf(glueLine), exact = glueLine)

    @RequestMapping(value = "search/time/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf(glueLine), exact = glueLine)

}

