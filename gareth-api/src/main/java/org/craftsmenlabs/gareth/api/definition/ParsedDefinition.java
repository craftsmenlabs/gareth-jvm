package org.craftsmenlabs.gareth.api.definition;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;

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
    Map<String, MethodDescriptor> getBaselineDefinitions();

    /**
     * Get assumption definitions
     *
     * @return
     */
    Map<String, MethodDescriptor> getAssumeDefinitions();

    /**
     * Get success definitions
     *
     * @return
     */
    Map<String, MethodDescriptor> getSuccessDefinitions();

    /**
     * Get failure definitions
     *
     * @return
     */
    Map<String, MethodDescriptor> getFailureDefinitions();

    /**
     * Get time definitions
     *
     * @return
     */
    Map<String, Duration> getTimeDefinitions();
}
