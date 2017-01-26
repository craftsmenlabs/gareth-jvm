package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.io.Serializable;

@Value
public class ExecutionResult implements Serializable {
    private ExperimentRunEnvironment environment;

    private ExecutionStatus status;
}
