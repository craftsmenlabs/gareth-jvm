package org.craftsmenlabs.gareth.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.binder.ExperimentEngineFeature;
import org.craftsmenlabs.gareth.rest.filter.CORSFilter;
import org.craftsmenlabs.gareth.rest.v1.resource.DefinitionsResource;
import org.craftsmenlabs.gareth.rest.v1.resource.ExperimentRerunResource;
import org.craftsmenlabs.gareth.rest.v1.resource.ExperimentResource;
import org.craftsmenlabs.gareth.rest.v1.resource.ExperimentRunResource;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class GarethApplication extends ResourceConfig {

    public GarethApplication(final ExperimentEngine experimentEngine) {
        registerInstances(new ExperimentEngineFeature(experimentEngine));
        register(JacksonJaxbJsonProvider.class);
        register(DeclarativeLinkingFeature.class);
        register(ExperimentResource.class);
        register(ExperimentRunResource.class);
        register(DefinitionsResource.class);
        register(ExperimentRerunResource.class);
        register(CORSFilter.class);
    }
}
