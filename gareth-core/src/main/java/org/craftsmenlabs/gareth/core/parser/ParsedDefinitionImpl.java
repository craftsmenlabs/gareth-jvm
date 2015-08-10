package org.craftsmenlabs.gareth.core.parser;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hylke on 10/08/15.
 */
@Getter
public class ParsedDefinitionImpl implements ParsedDefinition {

    private Map<String, Method> baselineDefinitions = new HashMap<>();
    private Map<String, Method> assumeDefinitions = new HashMap<>();
    private Map<String, Method> successDefinitions = new HashMap<>();
    private Map<String, Method> failureDefinitions = new HashMap<>();
    private Map<String, Duration> timeDefinitions = new HashMap<>();



}
