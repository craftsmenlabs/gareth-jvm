package org.craftsmenlabs.gareth.api;

import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;

import java.io.InputStream;

/**
 * Created by hylke on 10/08/15.
 */
public interface ExperimentEngine {

    /**
     * Load definitions, experiments and executes the experiments
     */
    void start();
}
