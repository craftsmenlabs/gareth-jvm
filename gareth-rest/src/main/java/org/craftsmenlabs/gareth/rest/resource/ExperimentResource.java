package org.craftsmenlabs.gareth.rest.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.listener.GarethServletContextListener;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hylke on 17/08/15.
 */
@Path("/experiments")
public class ExperimentResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        final ExperimentEngine experimentEngine = GarethServletContextListener.getExperimentEngine();
        return Response.status(200).entity(experimentEngine.getExperimentContexts()).build();
    }
}
