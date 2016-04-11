package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethContext {

    public static void main(final String[] args) throws Exception {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl();
        final ExperimentEngineConfig config = new ExperimentEngineConfigImpl.Builder().build();
        final ExperimentEngine engine = new ExperimentEngineImpl.Builder(config)
                .setRestServiceFactory(restServiceFactory).build();
        engine.start();
    }
}
