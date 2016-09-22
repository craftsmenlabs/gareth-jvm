package org.craftsmenlabs.gareth.core.registry;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownExperimentException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.Experiment;

import java.util.*;


public class ExperimentRegistry {

    @Getter
    private final Map<String, Experiment> experiments = new HashMap<>();

    public void addExperiment(final String experimentName, final Experiment experiment) {
        if (!experiments.containsKey(experimentName)) {
            experiments.put(experimentName, experiment);
        } else {
            throw new GarethAlreadyKnownExperimentException(String
                    .format("Experiment with name '%s' already known", experimentName));
        }
    }

    public Experiment getExperiment(final String experimentName) {
        return Optional
                .ofNullable(experiments.get(experimentName))
                .orElseThrow(() -> new GarethUnknownExperimentException(String
                        .format("Experiment '%s' unknown", experimentName)));
    }

    public List<Experiment> getAllExperiments() {
        return new ArrayList<>(experiments.values());
    }
}
