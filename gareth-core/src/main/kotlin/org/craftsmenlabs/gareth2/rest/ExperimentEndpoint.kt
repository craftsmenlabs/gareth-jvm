package org.craftsmenlabs.gareth2.rest

import org.craftsmenlabs.gareth.api.model.ExperimentCreateDTO
import org.craftsmenlabs.gareth.api.model.ExperimentDTO
import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDTOConverter
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/v1/experiments")
class ExperimentEndpoint constructor(@Autowired val experimentStorage: ExperimentStorage,
                                     @Autowired val converter: ExperimentDTOConverter,
                                     @Autowired val dateTimeService: DateTimeService) {


    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun upsert(@RequestBody dto: ExperimentCreateDTO): Experiment {
        val experiment = converter.createExperiment(dto)
        experimentStorage.save(experiment)
        return experiment
    }

    @RequestMapping(value = "{id}", method = arrayOf(RequestMethod.GET))
    fun get(@PathVariable("id") id: String): ExperimentDTO {
        return converter.createDTO(experimentStorage.getById(id))
    }

    @RequestMapping(value = "{id}/start", method = arrayOf(RequestMethod.PUT))
    fun start(@PathVariable("id") id: String): ExperimentDTO {
        val experiment = experimentStorage.getById(id)
        val updated = experiment.copy(timing = experiment.timing.copy(started = dateTimeService.now()))
        experimentStorage.save(updated)
        return converter.createDTO(experiment)
    }
}


