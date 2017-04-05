package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateUpdateDTO
import org.craftsmenlabs.gareth.services.TemplateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/templates")
class ExperimentTemplateEndpoint @Autowired constructor(val templateService: TemplateService) {

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    @CrossOrigin
    fun update(@RequestBody dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        return templateService.update(dto)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @CrossOrigin
    fun create(@RequestBody dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        return templateService.create(dto)
    }

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentTemplateDTO {
        return templateService.getTemplateById(id)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getByFilter(@RequestParam("name", required = false) name: String?): List<ExperimentTemplateDTO> {
        return templateService.getFiltered(name)
    }


}
