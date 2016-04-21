package org.craftsmenlabs.gareth.rest.example;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.craftsmenlabs.gareth.rest.RestServiceFactoryImpl;
import org.craftsmenlabs.gareth.rest.example.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.rest.example.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.rest.example.definition.SaleofFruit;
import org.craftsmenlabs.gareth.rest.example.definition.SampleDefinition;

/**
 * Hello world!
 */
public class GarethRestApplication {
    public static void main(final String[] args) {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory

        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addDefinitionClass(AnotherDefinition.class)
                .addDefinitionClass(SaleofFruit.class)
                .addInputStreams(GarethRestApplication.class.getClass()
                                                            .getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(true)
                .build();

        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                .Builder(experimentEngineConfig)
                .setRestServiceFactory(restServiceFactory)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build())
                .build();

        experimentEngine.start();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngine));
    }

    /**
     * Shutdown hook when application is stopped then also stop the experiment engine.
     */
    static class ShutdownHook extends Thread {

        private final ExperimentEngine experimentEngine;

        private ShutdownHook(final ExperimentEngine experimentEngine) {
            this.experimentEngine = experimentEngine;
        }

        @Override
        public void run() {
            experimentEngine.stop();
        }
    }
}
