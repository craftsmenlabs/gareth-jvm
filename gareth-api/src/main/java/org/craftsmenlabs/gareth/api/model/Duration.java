package org.craftsmenlabs.gareth.api.model;

import lombok.Value;

@Value
public class Duration
{
	private String unit;

	private long amount;
}
