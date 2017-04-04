package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.mongo.MongoEntityConverter
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/experiments")
class ExperimentEndpoint @Autowired constructor(val experimentStorage: ExperimentStorage,
                                                val converter: MongoEntityConverter,
                                                val dateTimeService: TimeService) {

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentDTO {
        return converter.toDTO(experimentStorage.getById(id))
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getFiltered(@RequestParam("created", required = false) ddMMYYYY: String?,
                    @RequestParam("completed", required = false) completed: Boolean?): List<ExperimentDTO> {
        val createdSince = if (ddMMYYYY == null) null else DateFormatUtils.parseDateStringToMidnight(ddMMYYYY)
        return experimentStorage.getFiltered(createdSince, completed).map { converter.toDTO(it) }
    }

    //TODO CrossOrigin annotation was added to make the old FE work: to be removed ASAP
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @CrossOrigin
    fun createExperiment(@RequestBody dto: ExperimentCreateDTO): ExperimentDTO {
        return converter.toDTO(experimentStorage.createExperiment(dto.templateId, dto.dueDate))
    }
}
