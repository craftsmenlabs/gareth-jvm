package org.craftsmenlabs.gareth.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class ExperimentDTO implements Serializable
{
	private String id;
	private String name;
	private int weight;
	private String baseline;
	private String assume;
	private String success;
	private String failure;
	private String time;

	private Date created;
	private Date ready;
	private Date started;
	private Date baselineExecuted;
	private Date completed;
	private boolean result;

	private Map<String, Serializable> environment;

}
