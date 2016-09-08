package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;

public class RestServiceFactoryImpl {

    public RestServiceImpl create(final ExperimentEngineImpl experimentEngine, final int port) {
        return new RestServiceImpl
                .Builder()
                .setExperimentEngine(experimentEngine)
                .setPortNumber(port)
                .build();
    }
}
