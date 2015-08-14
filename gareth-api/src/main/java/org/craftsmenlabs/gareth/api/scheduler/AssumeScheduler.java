package org.craftsmenlabs.gareth.api.scheduler;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by hylke on 14/08/15.
 */
public interface AssumeScheduler {

    /**
     * Schedule a assumption
     *
     * @param experimentContext
     */
    void schedule(final ExperimentContext experimentContext);

}
