package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.RestService;
import org.craftsmenlabs.gareth.rest.RestServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ApplicationContainer {

    private ExperimentEngine experimentEngine;

    @Autowired
    public ApplicationContainer(ExperimentEngine experimentEngine) {
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
