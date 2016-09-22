package org.craftsmenlabs.gareth.rest.binder;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;


public class ExperimentEngineBinder extends AbstractBinder {

    private final ExperimentEngineImpl experimentEngine;

    public ExperimentEngineBinder(final ExperimentEngineImpl experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @Override
    protected void configure() {
        bind(experimentEngine).to(ExperimentEngineImpl.class);
    }


}
