package org.craftsmenlabs.gareth.api.registry;

import org.craftsmenlabs.gareth.api.model.Experiment;

import java.util.List;


public interface ExperimentRegistry {

    void addExperiment(final String experimentName, final Experiment experiment);

    Experiment getExperiment(final String experimentName);

    List<Experiment> getAllExperiments();
}
