package org.craftsmenlabs.integration

import org.craftsmenlabs.gareth.model.GlueLineSearchResultDTO
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
class MockGlueLineMatcherEndpoint {

    @RequestMapping(value = "search/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf("sale of *"), exact = "sale of fruit")

    @RequestMapping(value = "search/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf("sale of fruit has risen by * per cent"), exact = "sale of fruit has risen by * per cent")

    @RequestMapping(value = "search/success/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf("send email to *"), exact = "send email to *")

    @RequestMapping(value = "search/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getFailureByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf("send email to *"), exact = "send email to *")

    @RequestMapping(value = "search/time/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): GlueLineSearchResultDTO =
            GlueLineSearchResultDTO(suggestions = listOf("* weeks?"), exact = "* weeks?")

}

