package org.craftsmenlabs.gareth.rest.binder;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Created by hylke on 19/08/15.
 */
public class ExperimentEngineBinder extends AbstractBinder {

    private final ExperimentEngine experimentEngine;

    public ExperimentEngineBinder(ExperimentEngine experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @Override
    protected void configure() {
        bind(experimentEngine).to(ExperimentEngine.class);
    }
}
