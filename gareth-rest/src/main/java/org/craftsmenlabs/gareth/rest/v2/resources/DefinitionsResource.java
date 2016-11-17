package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentToModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v2/definitions")
@EnableAutoConfiguration
@CrossOrigin
public class DefinitionsResource {

    private final ExperimentEngine experimentEngine;
    private final GlueLineMatcher glueLineMatcher;
    private final ExperimentToModelMapper mapper;

    @Autowired
    public DefinitionsResource(ExperimentEngine experimentEngine, GlueLineMatcher glueLineMatcher, ExperimentToModelMapper mapper) {
        this.experimentEngine = experimentEngine;
        this.glueLineMatcher = glueLineMatcher;
        this.mapper = mapper;

        this.glueLineMatcher.init(experimentEngine.getDefinitionRegistry()
                .getGlueLinesPerCategory());
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON})
    public String createNewExperimentRun(@RequestBody final Experiment experiment) {
        return experimentEngine.runExperiment(mapper.map(experiment));
    }

    @RequestMapping(
            value = "/{key}/{value}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public Map<String, List<String>> getMatches(final @PathVariable("key") String key, final @PathVariable("value") String value) {
        if (!glueLineMatcher.getGlueLineType(key).isPresent()) {
            throw new IllegalArgumentException("final part of path must be baseline, assumption, success, failure or time");
        }
        return glueLineMatcher.getMatches(key, value);
    }
}
