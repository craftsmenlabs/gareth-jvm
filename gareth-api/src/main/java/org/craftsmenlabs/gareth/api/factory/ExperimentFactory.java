package org.craftsmenlabs.gareth.api.factory;

import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.model.Experiment;

import java.io.InputStream;

/**
 * Created by hylke on 11/08/15.
 */
public interface ExperimentFactory {
    Experiment buildExperiment(InputStream inputStream) throws GarethExperimentParseException;
}
