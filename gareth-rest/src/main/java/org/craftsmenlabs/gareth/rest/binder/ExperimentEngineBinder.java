package org.craftsmenlabs.gareth.rest.binder;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.glassfish.hk2.utilities.binding.AbstractBinder;


public class ExperimentEngineBinder extends AbstractBinder {

    private final ExperimentEngine experimentEngine;

    public ExperimentEngineBinder(final ExperimentEngine experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @Override
    protected void configure() {
        bind(experimentEngine).to(ExperimentEngine.class);
    }


}
