package org.craftsmenlabs.gareth.api.definition;

import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;

/**
 * Created by hylke on 10/08/15.
 */
public interface ParsedDefinitionFactory {



    /**
     * Parse definitions into a parsed definition
     *
     * @param clazz The class that should be initialized
     * @throws GarethExperimentParseException
     *
     */
    ParsedDefinition parse(final Class clazz) throws GarethExperimentParseException;


}
