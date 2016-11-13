package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/v2/experiments-rerun")
@EnableAutoConfiguration
@CrossOrigin
public class ExperimentRerunResource {

    private final ExperimentEngine experimentEngine;

    @Autowired
    public ExperimentRerunResource(final ExperimentEngine experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @RequestMapping(
            value = "/{hash}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public boolean rerunExperiment(final @PathVariable("hash") String hash) {
        try {
            final ExperimentContext experimentContext = experimentEngine.findExperimentContextForHash(hash);
            experimentEngine.planExperimentContext(experimentContext);
            return true;
        } catch (final GarethUnknownExperimentException e) {
            throw new IllegalStateException("Not found");
        }
    }
}
