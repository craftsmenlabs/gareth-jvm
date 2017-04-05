package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.services.ExperimentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/experiments")
class ExperimentEndpoint @Autowired constructor(val service: ExperimentService) {

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentDTO {
        return service.get(id)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getFiltered(@RequestParam("created", required = false) ddMMYYYY: String?,
                    @RequestParam("completed", required = false) completed: Boolean?): List<ExperimentDTO> {
        return service.getFiltered(ddMMYYYY, completed)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun createExperiment(@RequestBody dto: ExperimentCreateDTO): ExperimentDTO {
        return service.createExperiment(dto)
    }
}
