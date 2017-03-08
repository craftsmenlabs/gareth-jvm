package org.craftsmenlabs.integration

import org.craftsmenlabs.BadRequestException
import org.craftsmenlabs.gareth.model.DefinitionInfo
import org.craftsmenlabs.gareth.model.Duration
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
class MockExecutionDefinitionEndPoint {

    @RequestMapping(value = "definitions/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String): DefinitionInfo {
        if (!glueLine.equals("sale of fruit"))
            throw BadRequestException("not a valid glueline")
        return DefinitionInfo(glueline = "^sale of (.*?)$", method = "getSaleOfItem", className = "org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")
    }

    @RequestMapping(value = "definitions/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String) =
            DefinitionInfo(glueline = "^sale of fruit has risen by (\\d+?) per cent$", method = "hasRisenByPercent", className = "org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")

    @RequestMapping(value = "definitions/success/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String) =
            DefinitionInfo(glueline = "^send email to (.*?)$", method = "sendText", className = "org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")

    @RequestMapping(value = "definitions/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getFailureByGlueline(@PathVariable("glueline") glueLine: String) =
            DefinitionInfo(glueline = "^send email to (.*?)$", method = "sendText", className = "org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")

    @RequestMapping(value = "definitions/time/{glueline}", method = arrayOf(RequestMethod.GET))
    fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): Duration =
            Duration("SECONDS", 2)
}