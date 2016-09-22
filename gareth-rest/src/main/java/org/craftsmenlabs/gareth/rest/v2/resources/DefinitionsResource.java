package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentToModelMapper2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/definitions")
@EnableAutoConfiguration
@CrossOrigin
public class DefinitionsResource {

    private final ExperimentEngineImpl experimentEngine;
    private final GlueLineMatcher glueLineMatcher = new GlueLineMatcher();
    private final ExperimentToModelMapper2 mapper;

    @Autowired
    public DefinitionsResource(ExperimentEngineImpl experimentEngine, ExperimentToModelMapper2 mapper) {
        this.experimentEngine = experimentEngine;
        this.mapper = mapper;

        glueLineMatcher.init(experimentEngine.getDefinitionRegistry()
                .getGlueLinesPerCategory());
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON})
    public String createNewExperimentRun(final Experiment experiment) {
        return experimentEngine.runExperiment(mapper.map(experiment));
    }

    @RequestMapping(
            value = "/{key}/{value}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public Response getMatches(final @PathVariable("key") String key, final @PathVariable("value") String value) {
        if (!glueLineMatcher.getGlueLineType(key).isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("final part of path must be baseline, assumption, success, failure or time").build();
        }

        final Response response = produceResponse(glueLineMatcher.getMatches(key, value));
        return response;
    }

    private Response produceResponse(Map<String, List<String>> matches) {
        return Response
                .status(200)
                .entity(new GenericEntity<Map<String, List<String>>>(matches) {
                })
                .build();
    }
}
