package org.craftsmenlabs.gareth.rest

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.ExperimentDTOConverter
import org.craftsmenlabs.gareth.time.DateFormatUtils
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/templates")
class ExperimentTemplateEndpoint @Autowired constructor(val experimentStorage: ExperimentStorage,
                                                        val converter: ExperimentDTOConverter,
                                                        val dateTimeService: TimeService) {

    //TODO CrossOrigin annotation was added to make the old FE work: to be removed ASAP
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    @CrossOrigin
    fun upsert(@RequestBody dto: ExperimentCreateDTO): ExperimentDTO {
        val experiment = converter.createExperiment(dto)
        val saved = experimentStorage.save(experiment)
        return converter.createDTO(saved)
    }

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: Long): ExperimentDTO {
        return converter.createDTO(experimentStorage.getById(id))
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getFiltered(@RequestParam("created", required = false) ddMMYYYY: String?,
                    @RequestParam("completed", required = false) completed: Boolean?): List<ExperimentDTO> {
        val createdSince = if (ddMMYYYY == null) null else DateFormatUtils.parseDateStringToMidnight(ddMMYYYY)
        return experimentStorage.getFiltered(createdSince, completed).map { converter.createDTO(it) }
    }

    //TODO CrossOrigin annotation was added to make the old FE work: to be removed ASAP
    @RequestMapping(value = "{id}/start", method = arrayOf(RequestMethod.PUT))
    @CrossOrigin
    fun start(@PathVariable("id") id: Long): ExperimentDTO {
        val experiment = experimentStorage.getById(id)
        if (experiment.timing.ready == null) {
            throw IllegalStateException("You cannot start an experiment that is not ready.")
        }
        val updated = experiment.copy(timing = experiment.timing.copy(started = dateTimeService.now()))
        experimentStorage.save(updated)
        return converter.createDTO(updated)
    }
}
