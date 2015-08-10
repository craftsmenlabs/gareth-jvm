package org.craftsmenlabs.gareth.api.registry;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by hylke on 10/08/15.
 */
public interface DefinitionRegistry {

    /**
     * Get method for baseline
     * @param glueLine
     * @return
     */
    Method getMethodForBaseline(final String glueLine);

    /**
     * Get method assume
     * @param glueLine
     * @return
     */
    Method getMethodForAssume(final String glueLine);

    /**
     * Get method for success
     * @param glueLine
     * @return
     */
    Method getMethodForSuccess(final String glueLine);


    /**
     * Get method for failure
     * @param glueLine
     * @return
     */
    Method getMethodForFailure(final String glueLine);

    /**
     * Get duration for time
     *
     * @param glueLine
     * @return
     */
    Duration getDurationForTime(final String glueLine);

    /**
     * Add method for baseline
     * @param glueLine
     * @param method
     */
    void addMethodForBaseline(final String glueLine, final Method method);

    /**
     * Add method for assume
     * @param glueLine
     * @param method
     */
    void addMethodForAssume(final String glueLine, final Method method);

    /**
     * Add method for success
     * @param glueLine
     * @param method
     */
    void addMethodForSuccess(final String glueLine, final Method method);

    /**
     * Add method for failure
     * @param glueLine
     * @param method
     */
    void addMethodForFailure(final String glueLine, final Method method);

    /**
     * Add duration for time
     * @param glueLine
     * @param duration
     */
    void addDurationForTime(final String glueLine, final Duration duration);


}
