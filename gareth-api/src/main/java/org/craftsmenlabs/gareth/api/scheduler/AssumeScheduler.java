package org.craftsmenlabs.gareth.api.scheduler;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;

public interface AssumeScheduler {

    /**
     * Schedule a assumption
     *
     * @param experimentRunContext experiment run context
     * @param experimentEngine     experiment engine
     */
    void schedule(final ExperimentRunContext experimentRunContext, final ExperimentEngine experimentEngine);

}
