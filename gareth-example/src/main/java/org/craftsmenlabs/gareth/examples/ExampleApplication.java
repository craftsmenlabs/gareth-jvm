package org.craftsmenlabs.gareth.examples;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.craftsmenlabs.gareth.examples.definition.SampleDefinition;

/**
 * Example Gareth application
 */
public class ExampleApplication {


    public static void main(final String[] args) {
        final ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
                .build();
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addInputStreams(ExampleApplication.class.getClass()
                                                         .getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(false)
                .build();
        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                .Builder(experimentEngineConfig)
                .setExperimentEnginePersistence(experimentEnginePersistence)
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
