package org.craftsmenlabs.gareth.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.binder.ExperimentEngineFeature;
import org.craftsmenlabs.gareth.rest.filter.CORSFilter;
import org.craftsmenlabs.gareth.rest.resource.ExperimentRerunResource;
import org.craftsmenlabs.gareth.rest.resource.ExperimentResource;
import org.craftsmenlabs.gareth.rest.resource.ExperimentRunResource;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethApplication extends ResourceConfig {


    public GarethApplication(final ExperimentEngine experimentEngine) {
        registerInstances(new ExperimentEngineFeature(experimentEngine));
        register(JacksonJaxbJsonProvider.class);
        register(ExperimentResource.class);
        register(ExperimentRunResource.class);
        register(ExperimentRerunResource.class);
        register(CORSFilter.class);


    }


}
