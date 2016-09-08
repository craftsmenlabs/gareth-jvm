package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.application.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.application.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.application.definition.SaleofFruit;
import org.craftsmenlabs.gareth.application.definition.SampleDefinition;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImplBuilder;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.craftsmenlabs.gareth.rest.RestServiceFactoryImpl;
import org.craftsmenlabs.gareth.rest.RestServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationContainer {

    @PostConstruct
    public void init() throws Exception {

        final ExperimentEngineConfigImpl experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addDefinitionClass(AnotherDefinition.class)
                .addDefinitionClass(SaleofFruit.class)
                .addInputStreams(this.getClass()
                        .getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(true)
                .build();

        final ExperimentEngineImpl experimentEngine = new ExperimentEngineImplBuilder(experimentEngineConfig)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build())
                .build();

        experimentEngine.start();
        //experimentEngine.runExperiment(createExperiment());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngine));

        final RestServiceFactoryImpl restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory
        final RestServiceImpl restService = restServiceFactory.create(experimentEngine, 8888);
        restService.start();
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

        private final ExperimentEngineImpl experimentEngine;

        private ShutdownHook(final ExperimentEngineImpl experimentEngine) {
            this.experimentEngine = experimentEngine;
        }

        @Override
        public void run() {
            experimentEngine.stop();
        }
    }
}
