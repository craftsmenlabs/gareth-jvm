package org.craftsmenlabs.gareth.rest.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
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

/**
 * Created by hylke on 13/10/15.
 */
@Path("/experimentruns")
public class ExperimentRunResource {

    @Inject
    private ExperimentEngine experimentEngine;

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

    private List<ExperimentRun> assembleExperiments(final List<ExperimentRunContext> experimentRunContexts) {
        final Assembler<ExperimentRunContext, ExperimentRun> assembler = new ExperimentRunAssembler();
        final List<ExperimentRun> experimentRuns = new ArrayList<>();
        for (final ExperimentRunContext experimentRunContext : experimentRunContexts) {
            experimentRuns.add(assembler.assembleOutbound(experimentRunContext));
        }
        return experimentRuns;
    }
}
