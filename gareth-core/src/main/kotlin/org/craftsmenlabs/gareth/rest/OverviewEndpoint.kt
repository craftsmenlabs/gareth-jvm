package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.OverviewDTO
import org.craftsmenlabs.gareth.providers.OverviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/v1/stats")
class OverviewEndpoint @Autowired constructor(val overviewService: OverviewService) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getAll(): List<OverviewDTO> {
        return overviewService.getAll()
    }

}
