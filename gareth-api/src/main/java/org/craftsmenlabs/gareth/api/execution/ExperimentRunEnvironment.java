package org.craftsmenlabs.gareth.api.execution;

import java.util.List;
import lombok.Value;

@Value
public class ExperimentRunEnvironment
{

	private List<EnvironmentItem> items;
}
