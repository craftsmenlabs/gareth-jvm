package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.assembler.ExperimentAssembler;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v2/experiments")
@EnableAutoConfiguration
@CrossOrigin
public class ExperimentResource {

    private ExperimentEngineImpl experimentEngine;

    @Autowired
    public ExperimentResource(ExperimentEngineImpl experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public Response get() {
        return Response
                .status(200)
                .entity(new GenericEntity<List<Experiment>>(assembleExperiments(experimentEngine
                        .getExperimentContexts())) {
                })
                .build();
    }

    private List<Experiment> assembleExperiments(final List<ExperimentContextImpl> experimentContexts) {
        final Assembler<ExperimentContextImpl, Experiment> assembler = new ExperimentAssembler();
        final List<Experiment> experiments = new ArrayList<>();
        for (final ExperimentContextImpl experimentContext : experimentContexts) {
            experiments.add(assembler.assembleOutbound(experimentContext));
        }

        return experiments;
    }
}
