package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.io.Serializable;

@Value
public class ExecutionRequest implements Serializable {

    private ExperimentRunEnvironment environment;

    private String glueLine;

}
