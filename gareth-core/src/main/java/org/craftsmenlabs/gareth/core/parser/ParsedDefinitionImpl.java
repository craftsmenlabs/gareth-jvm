package org.craftsmenlabs.gareth.core.parser;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Getter
public class ParsedDefinitionImpl implements ParsedDefinition {

    private final Map<String, MethodDescriptor> baselineDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> assumeDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> successDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> failureDefinitions = new HashMap<>();
    private final Map<String, Duration> timeDefinitions = new HashMap<>();


}
