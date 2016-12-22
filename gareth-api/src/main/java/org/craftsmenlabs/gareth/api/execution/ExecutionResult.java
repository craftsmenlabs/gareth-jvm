package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

@Value
public class ExecutionResult {
    private ExperimentRunEnvironment environment;

    private ExecutionStatus status;
}
