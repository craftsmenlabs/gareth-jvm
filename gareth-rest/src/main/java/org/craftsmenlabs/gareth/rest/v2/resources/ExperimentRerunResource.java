package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/v2/experiments-rerun")
@EnableAutoConfiguration
@CrossOrigin
public class ExperimentRerunResource {

    private final ExperimentEngineImpl experimentEngine;

    @Autowired
    public ExperimentRerunResource(final ExperimentEngineImpl experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @RequestMapping(
            value = "/{hash}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public Response rerunExperiment(final @PathVariable("hash") String hash) {
        Response response = null;
        try {
            final ExperimentContextImpl experimentContext = experimentEngine.findExperimentContextForHash(hash);
            experimentEngine.planExperimentContext(experimentContext);
            response = Response.accepted().build();
        } catch (final GarethUnknownExperimentException e) {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }
}
