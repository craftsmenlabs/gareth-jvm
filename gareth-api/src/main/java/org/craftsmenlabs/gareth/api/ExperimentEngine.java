package org.craftsmenlabs.gareth.api;

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
    void loadDefinition(final Class clazz);

    /**
     * Load a experiment from input stream
     *
     * @param inputStream
     */
    void loadExperiment(final InputStream inputStream);
}
