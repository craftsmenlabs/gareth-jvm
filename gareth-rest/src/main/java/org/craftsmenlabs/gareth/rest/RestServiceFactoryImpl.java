package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.rest.RestService;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;

public class RestServiceFactoryImpl implements RestServiceFactory {

    @Override
    public RestService create(final ExperimentEngine experimentEngine, final int port) {
        return new RestServiceImpl
                .Builder()
                .setExperimentEngine(experimentEngine)
                .build();
    }
}
