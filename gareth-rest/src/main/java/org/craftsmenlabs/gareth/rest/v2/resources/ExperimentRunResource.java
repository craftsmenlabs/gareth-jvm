package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.assembler.ExperimentRunAssembler;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v2/experimentruns")
@EnableAutoConfiguration
@CrossOrigin
public class ExperimentRunResource {

    private final ExperimentEngineImpl experimentEngine;

    @Autowired
    public ExperimentRunResource(ExperimentEngineImpl experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @RequestMapping(
            value = "/{hash}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public Response get(final @PathVariable("hash") String hash) {
        return Response
                .status(200)
                .entity(new GenericEntity<List<ExperimentRun>>(assembleExperiments(experimentEngine
                        .findExperimentRunContextsForHash(hash))) {
                })
                .build();
    }

    private List<ExperimentRun> assembleExperiments(final List<ExperimentRunContextImpl> experimentRunContexts) {
        final Assembler<ExperimentRunContextImpl, ExperimentRun> assembler = new ExperimentRunAssembler();
        final List<ExperimentRun> experimentRuns = new ArrayList<>();
        for (final ExperimentRunContextImpl experimentRunContext : experimentRunContexts) {
            experimentRuns.add(assembler.assembleOutbound(experimentRunContext));
        }
        return experimentRuns;
    }
}
