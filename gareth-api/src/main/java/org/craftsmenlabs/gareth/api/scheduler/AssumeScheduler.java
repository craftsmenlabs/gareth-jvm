package org.craftsmenlabs.gareth.api.scheduler;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by hylke on 14/08/15.
 */
public interface AssumeScheduler {

    /**
     * Schedule a assumption
     *
     * @param experimentRunContext experiment run context
     * @param experimentEngine experiment engine
     */
    void schedule(final ExperimentRunContext experimentRunContext,final ExperimentEngine experimentEngine);

}
