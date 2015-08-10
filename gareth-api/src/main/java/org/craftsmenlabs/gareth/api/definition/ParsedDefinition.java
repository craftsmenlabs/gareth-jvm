package org.craftsmenlabs.gareth.api.definition;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;

/**
 * Created by hylke on 10/08/15.
 */
public interface ParsedDefinition {

    /**
     * Get baseline definitions
     *
     * @return
     */
    Map<String, Method> getBaselineDefinitions();

    /**
     * Get assumption definitions
     * @return
     */
    Map<String, Method> getAssumeDefinitions();

    /**
     * Get success definitions
     *
     * @return
     */
    Map<String, Method> getSuccessDefinitions();

    /**
     * Get failure definitions
     *
     * @return
     */
    Map<String, Method> getFailureDefinitions();

    /**
     * Get time definitions
     *
     * @return
     */
    Map<String, Duration> getTimeDefinitions();
}
