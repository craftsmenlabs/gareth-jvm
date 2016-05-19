package org.craftsmenlabs.gareth.api.factory;

import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.model.Experiment;

import java.io.InputStream;

public interface ExperimentFactory {
    Experiment buildExperiment(InputStream inputStream) throws GarethExperimentParseException;
}
