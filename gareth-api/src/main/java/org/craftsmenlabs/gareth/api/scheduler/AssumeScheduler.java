package org.craftsmenlabs.gareth.api.scheduler;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by hylke on 14/08/15.
 */
public interface AssumeScheduler {

    /**
     * Schedule a assumption
     *
     * @param assumeMethod
     * @param duration
     * @param successMethod
     * @param failureMethod
     */
    void schedule(final Method assumeMethod, final Duration duration, final Method successMethod, final Method failureMethod);

}
