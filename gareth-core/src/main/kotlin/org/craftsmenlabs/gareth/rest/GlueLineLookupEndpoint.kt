package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.model.GlueLineType
import org.craftsmenlabs.gareth.services.GluelineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/")
class GlueLineLookupEndpoint @Autowired constructor(val service: GluelineService) {

    @RequestMapping(value = "glueline", method = arrayOf(RequestMethod.GET))
    fun lookupGlueline(@RequestParam("type") glueLine: GlueLineType,
                       @RequestParam("content") content: String): GlueLineSearchResultDTO =
            service.lookupGlueline(glueLine, content)
}