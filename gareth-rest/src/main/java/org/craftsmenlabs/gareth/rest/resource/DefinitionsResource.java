package org.craftsmenlabs.gareth.rest.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.craftsmenlabs.gareth.rest.v1.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v1.media.GarethMediaType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Path("/definitions")
public class DefinitionsResource {

    @Inject
    private ExperimentEngine experimentEngine;
    private GlueLineMatcher glueLineMatcher = new GlueLineMatcher();

    @PostConstruct
    public void init() {
        glueLineMatcher.init(experimentEngine.getDefinitionRegistry()
                                             .getGlueLinesPerCategory());
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public String createNewExperimentRun(final Experiment experiment) {
        org.craftsmenlabs.gareth.api.model.Experiment model = new org.craftsmenlabs.gareth.api.model.Experiment();
        model.setExperimentName(experiment.getExperimentName());
        AssumptionBlock block = new AssumptionBlock();
        block.setBaseline(experiment.getBaselineGlueLine());
        block.setAssumption(experiment.getAssumeGlueLine());
        block.setFailure(experiment.getFailureGlueLine());
        block.setSuccess(experiment.getSuccessGlueLine());
        block.setTime(experiment.getTimeGlueLine());
        model.setAssumptionBlockList(Arrays.asList(block));
        return experimentEngine.runExperiment(model);
    }

    @Path("{key}/{value}")
    @GET
    @Produces({GarethMediaType.APPLICATION_JSON_EXPERIMENTS_V1, MediaType.APPLICATION_JSON})
    public Response getMatches(final @PathParam("key") String key, final @PathParam("value") String value) {
        if (!glueLineMatcher.getGlueLineType(key).isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("final part of path must be baseline, assume, succcess, failure or time").build();
        }

        return produceResponse(glueLineMatcher.getMatches(key, value));
    }

    private Response produceResponse(Map<String, List<String>> matches) {
        return Response
                .status(200)
                .entity(new GenericEntity<Map<String, List<String>>>(matches) {
                })
                .build();
    }

}
