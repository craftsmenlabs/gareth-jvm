package org.craftsmenlabs.gareth.api.model;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class ExperimentCreateDTO implements Serializable
{
	private String name;
	private int weight;
	private String baseline;
	private String assume;
	private String success;
	private String failure;
	private String time;

	private Map<String, Serializable> environment;

}
