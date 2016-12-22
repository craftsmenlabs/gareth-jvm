package org.craftsmenlabs.gareth.api.execution;

public enum ExecutionStatus {
    /**
     * return after successfully executing the baseline step
     */
    RUNNING,
    /**
     * Returned after the assume step was evaluated successfully
     */
    SUCCESS,
    /**
     * Returned after the assume step was evaluated with a failure
     */
    FAILURE
}
