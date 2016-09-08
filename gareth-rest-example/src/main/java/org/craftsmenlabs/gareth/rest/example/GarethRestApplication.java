package org.craftsmenlabs.gareth.rest.example;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImplBuilder;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.craftsmenlabs.gareth.rest.RestServiceFactoryImpl;
import org.craftsmenlabs.gareth.rest.example.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.rest.example.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.rest.example.definition.SaleofFruit;
import org.craftsmenlabs.gareth.rest.example.definition.SampleDefinition;

import java.util.ArrayList;
import java.util.List;

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

        final ExperimentEngine experimentEngine = new ExperimentEngineImplBuilder(experimentEngineConfig)
                .setRestServiceFactory(restServiceFactory)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build())
                .build();

        experimentEngine.start();
        //experimentEngine.runExperiment(createExperiment());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngine));
    }

    private static Experiment createExperiment() {
        Experiment experiment = new Experiment();
        experiment.setExperimentName("standalone");
        experiment.setAssumptionBlockList(createAssumptionBlock());
        return experiment;
    }

    private static List<AssumptionBlock> createAssumptionBlock() {
        ArrayList<AssumptionBlock> blocks = new ArrayList<>();
        AssumptionBlock block = new AssumptionBlock();
        block.setAssumption("has risen by 10 per cent");
        block.setBaseline("sale of bananas");
        block.setFailure("Blame the suits");
        block.setTime("3 seconds");
        block.setSuccess("send cake to developers");
        blocks.add(block);
        return blocks;
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
