package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.util.List;

@Value
public class ExperimentRunEnvironment {

    private List<EnvironmentItem> items;
}
