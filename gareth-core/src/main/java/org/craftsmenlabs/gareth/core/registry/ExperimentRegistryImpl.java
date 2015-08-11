package org.craftsmenlabs.gareth.core.registry;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownExperimentException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by hylke on 11/08/15.
 */
public class ExperimentRegistryImpl implements ExperimentRegistry {

    @Getter
    private final Map<String, Experiment> experiments = new HashMap<>();

    @Override
    public void addExperiment(final String experimentName, final Experiment experiment) {
        if (!experiments.containsKey(experimentName)) {
            experiments.put(experimentName, experiment);
        } else {
            throw new GarethAlreadyKnownExperimentException(String.format("Experiment with name '%s' already known", experimentName));
        }
    }

    @Override
    public Experiment getExperiment(final String experimentName) {
        return Optional
                .ofNullable(experiments.get(experimentName))
                .orElseThrow(() -> new GarethUnknownExperimentException(String.format("Experiment '%s' unknown", experimentName)));
    }
}
