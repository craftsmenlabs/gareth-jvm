package org.craftsmenlabs.gareth.validator.rest

import org.craftsmenlabs.gareth.validator.definitions.DefinitionRegistryService
import org.craftsmenlabs.gareth.validator.model.AssumeExecutionResult
import org.craftsmenlabs.gareth.validator.model.BaselineExecutionResult
import org.craftsmenlabs.gareth.validator.model.DefinitionRegistryDTO
import org.craftsmenlabs.gareth.validator.model.ExecutionRequest
import org.craftsmenlabs.gareth.validator.services.ExperimentExecutionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("gareth/validator/v1/execution")
class GarethHub @Autowired constructor(private val executionService: ExperimentExecutionService,
                                       private val definitionRegistryService: DefinitionRegistryService) : GarethHubClient {

    @RequestMapping(value = "/baselinestatus/{id}", method = arrayOf(RequestMethod.PUT))
    override fun updateRegistryForClient(@RequestBody registry: DefinitionRegistryDTO) {
        definitionRegistryService.registerDefinitionsForClient("",registry)
    }


    @RequestMapping(value = "/baselinestatus/{id}", method = arrayOf(RequestMethod.PUT))
    override fun updateBaselineStatus(@RequestBody result: BaselineExecutionResult) {
        executionService.setBaselineExecutionResult(result)
    }

    @RequestMapping(value = "/assumestatus/{id}", method = arrayOf(RequestMethod.PUT))
    override fun updateAssumeStatus(@RequestBody result: AssumeExecutionResult) {
        executionService.setAssumeExecutionResult(result)
    }

    @RequestMapping(value = "/baselines/{id}", method = arrayOf(RequestMethod.GET))
    override fun getBaselinesToExecute(@PathVariable("id") id: String): List<ExecutionRequest> {
        return executionService.getBaselinesToExecute(id)
    }

    @RequestMapping(value = "/assumes/{id}", method = arrayOf(RequestMethod.GET))
    override fun getAssumesToExecute(@PathVariable("id") id: String): List<ExecutionRequest> {
        return executionService.getAssumesToExecute(id)
    }

}
