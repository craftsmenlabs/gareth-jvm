package org.craftsmenlabs.gareth.api.execution;

import lombok.Value;

import java.util.List;

@Value
public class GlueLineSearchResult {
    private List<String> suggestions;

    private String exact;
}
