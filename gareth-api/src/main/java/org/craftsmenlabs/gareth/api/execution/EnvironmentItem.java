package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

@Value
public class EnvironmentItem {
    private String key;

    private String value;

    private ItemType itemType;
}
