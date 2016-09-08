package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.rest.RestServiceFactoryImpl;
import org.craftsmenlabs.gareth.rest.RestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationContainer {

    private ExperimentEngineConfigImpl experimentEngineConfigImpl;
    private ExperimentEngineImpl experimentEngineImpl;

    @Autowired
    public ApplicationContainer(ExperimentEngineConfigImpl experimentEngineConfigImpl, ExperimentEngineImpl experimentEngineImpl) {
        this.experimentEngineConfigImpl = experimentEngineConfigImpl;
        this.experimentEngineImpl = experimentEngineImpl;
    }

    @PostConstruct
    public void init() throws Exception {

        experimentEngineImpl.start();
        //experimentEngine.runExperiment(createExperiment());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngineImpl));

        final RestServiceFactoryImpl restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory
        final RestServiceImpl restService = restServiceFactory.create(experimentEngineImpl, 8888);
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
