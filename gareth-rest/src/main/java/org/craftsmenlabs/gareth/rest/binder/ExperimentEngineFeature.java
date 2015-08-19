package org.craftsmenlabs.gareth.rest.binder;

import org.craftsmenlabs.gareth.api.ExperimentEngine;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Created by hylke on 19/08/15.
 */
public class ExperimentEngineFeature implements Feature {

    private final ExperimentEngine experimentEngine;

    public ExperimentEngineFeature(ExperimentEngine experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @Override
    public boolean configure(final FeatureContext featureContext) {
        featureContext.register(new ExperimentEngineBinder(this.experimentEngine));
        return true;
    }
}
