package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class ExperimentRunEnvironment implements Serializable {

    private List<EnvironmentItem> items;
}
