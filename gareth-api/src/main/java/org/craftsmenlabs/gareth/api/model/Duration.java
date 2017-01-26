package org.craftsmenlabs.gareth.api.model;

import lombok.Value;

import java.io.Serializable;

@Value
public class Duration implements Serializable {
    private String unit;

    private long amount;
}
