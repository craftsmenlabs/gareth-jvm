package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateUpdateDTO
import org.craftsmenlabs.gareth.mongo.MongoStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/templates")
class ExperimentTemplateEndpoint @Autowired constructor(val storage: MongoStorage) {

    //TODO CrossOrigin annotation was added to make the old FE work: to be removed ASAP
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    @CrossOrigin
    fun update(@RequestBody dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO {
        return storage.updateTemplate(dto)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @CrossOrigin
    fun create(@RequestBody dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO {
        return storage.createTemplate(dto)
    }

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentTemplateDTO {
        return storage.getTemplateById(id)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getByFilter(@RequestParam("name", required = false) name: String?): List<ExperimentTemplateDTO> {
        return if (name != null) listOf(storage.getTemplateByName(name)) else storage.getAllTemplates()
    }


}
