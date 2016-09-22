package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.rest.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationContainer {

    private ExperimentEngineConfig experimentEngineConfig;
    private ExperimentEngine experimentEngine;

    @Autowired
    public ApplicationContainer(ExperimentEngineConfig experimentEngineConfig, ExperimentEngine experimentEngine) {
        this.experimentEngineConfig = experimentEngineConfig;
        this.experimentEngine = experimentEngine;
    }

    @PostConstruct
    public void init() throws Exception {

        experimentEngine.start();
        //experimentEngine.runExperiment(createExperiment());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngine));

        final RestServiceFactory restServiceFactory = new RestServiceFactory(); // Create a new rest service factory
        final RestService restService = restServiceFactory.create(experimentEngine, 8888);
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
