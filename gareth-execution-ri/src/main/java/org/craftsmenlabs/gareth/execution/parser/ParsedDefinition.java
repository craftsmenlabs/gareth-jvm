package org.craftsmenlabs.gareth.execution.parser;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.craftsmenlabs.gareth.execution.invoker.RegexMethodDescriptor;
import lombok.Getter;

/**
 * Represents a mapping between glueline text and a corresponding method in a definition files.
 * A single Definition file may contain more than one baselineDefinitions, for example.
 */
@Getter
public class ParsedDefinition {

    private final Map<String, RegexMethodDescriptor> baselineDefinitions = new HashMap<>();
    private final Map<String, RegexMethodDescriptor> assumeDefinitions = new HashMap<>();
    private final Map<String, RegexMethodDescriptor> successDefinitions = new HashMap<>();
    private final Map<String, RegexMethodDescriptor> failureDefinitions = new HashMap<>();
    private final Map<String, Duration> timeDefinitions = new HashMap<>();
}
