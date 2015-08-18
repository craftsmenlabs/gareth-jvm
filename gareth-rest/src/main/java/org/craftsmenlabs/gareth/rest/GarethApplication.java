package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.rest.resource.ExperimentResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resourceClasses = new HashSet<Class<?>>();
        resourceClasses.add(ExperimentResource.class);
        return resourceClasses;
    }


}
