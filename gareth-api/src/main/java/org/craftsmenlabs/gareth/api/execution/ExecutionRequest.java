package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

@Value
public class ExecutionRequest {

    private ExperimentRunEnvironment environment;

    private String glueLine;

}
