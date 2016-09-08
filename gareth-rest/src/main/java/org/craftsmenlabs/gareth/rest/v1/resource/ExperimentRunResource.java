package org.craftsmenlabs.gareth.rest.v1.resource;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v1.assembler.ExperimentRunAssembler;
import org.craftsmenlabs.gareth.rest.v1.entity.ExperimentRun;
import org.craftsmenlabs.gareth.rest.v1.media.GarethMediaType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/experimentruns")
public class ExperimentRunResource {

    @Inject
    private ExperimentEngineImpl experimentEngine;

    @GET
    @Produces({GarethMediaType.APPLICATION_JSON_EXPERIMENTRUNS_V1, MediaType.APPLICATION_JSON})
    @Path("{hash}")
    public Response get(final @PathParam("hash") String hash) {
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
