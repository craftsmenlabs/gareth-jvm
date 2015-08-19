package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.binder.ExperimentEngineFeature;
import org.craftsmenlabs.gareth.rest.resource.ExperimentResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethApplication extends ResourceConfig {


    public GarethApplication(final ExperimentEngine experimentEngine) {
        registerInstances(new ExperimentEngineFeature(experimentEngine));
        register(ExperimentResource.class);
    }


}
