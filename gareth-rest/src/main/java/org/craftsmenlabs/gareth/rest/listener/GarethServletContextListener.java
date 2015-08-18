package org.craftsmenlabs.gareth.rest.listener;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by hylke on 18/08/15.
 */
public class GarethServletContextListener implements ServletContextListener {

    private static ExperimentEngine experimentEngine;

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        if (null == experimentEngine) {
            final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                    .Builder()
                    .setIgnoreInvocationExceptions(true)
                    .build();
            experimentEngine = new ExperimentEngineImpl
                    .Builder(experimentEngineConfig)
                    .build();
            experimentEngine.start();
        }

    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        experimentEngine = null;
    }

    public static ExperimentEngine getExperimentEngine() {
        if (experimentEngine != null) {
            return experimentEngine;
        }
        throw new IllegalStateException("Experiment engine is not initialized");
    }
}
