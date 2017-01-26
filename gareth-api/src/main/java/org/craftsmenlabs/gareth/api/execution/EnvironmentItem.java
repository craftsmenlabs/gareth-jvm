package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.io.Serializable;

@Value
public class EnvironmentItem implements Serializable {
    private String key;

    private String value;

    private ItemType itemType;
}
