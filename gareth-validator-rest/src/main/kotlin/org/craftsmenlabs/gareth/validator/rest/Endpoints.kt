package org.craftsmenlabs.gareth.validator.rest


import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.services.ExperimentService
import org.craftsmenlabs.gareth.services.GluelineService
import org.craftsmenlabs.gareth.services.OverviewService
import org.craftsmenlabs.gareth.services.TemplateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("gareth/validator/v1/experiments")
class ExperimentEndpoint @Autowired constructor(val experimentService: ExperimentService) {

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentDTO {
        return experimentService.getExperimentById(id)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getFiltered(@RequestParam("created", required = false) ddMMYYYY: String?,
                    @RequestParam("completed", required = false) completed: Boolean?): List<ExperimentDTO> {
        return experimentService.getFiltered("acme", ddMMYYYY, completed)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun createExperiment(@RequestBody dto: ExperimentCreateDTO): ExperimentDTO {
        return experimentService.createExperiment(dto)
    }
}

@RestController
@RequestMapping("gareth/validator/v1/templates")
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

@RestController
@RequestMapping("gareth/validator/v1/")
class GlueLineLookupEndpoint @Autowired constructor(val service: GluelineService) {

    @RequestMapping(value = "glueline", method = arrayOf(RequestMethod.GET))
    fun lookupGlueline(@RequestParam("type") glueLine: GlueLineType,
                       @RequestParam("content") content: String): GlueLineSearchResultDTO =
            service.lookupGlueline(glueLine, content)
}

@RestController
@RequestMapping("gareth/validator/v1/overview/")
class OverviewEndpoint @Autowired constructor(val overviewService: OverviewService) {

    @RequestMapping(value = "{projectId}", method = arrayOf(RequestMethod.GET))
    fun getAll(@PathVariable("projectId") projectId: String): List<OverviewDTO> {
        return overviewService.getAllForProject(projectId)
    }
}

