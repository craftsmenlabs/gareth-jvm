package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngine;

public class RestServiceFactory {

    public RestService create(final ExperimentEngine experimentEngine, final int port) {
        return new RestService
                .Builder()
                .setExperimentEngine(experimentEngine)
                .setPortNumber(port)
                .build();
    }
}
