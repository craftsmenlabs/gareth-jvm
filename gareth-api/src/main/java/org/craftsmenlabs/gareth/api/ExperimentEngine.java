package org.craftsmenlabs.gareth.api;

import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;

import java.io.InputStream;

/**
 * Created by hylke on 10/08/15.
 */
public interface ExperimentEngine {

    /**
     * Load class containing definitions
     *
     * @param clazz
     */
    void registerDefinition(final Class clazz) throws GarethExperimentParseException;

    /**
     * Load a experiment from input stream
     *
     * @param inputStream
     */
    void registerExperiment(final InputStream inputStream);
}
