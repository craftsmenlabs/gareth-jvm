package org.craftsmenlabs.gareth.core.persist;

import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;


public interface ExperimentEnginePersistence {

    void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException;

    void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException;

    ExperimentStateChangeListener getExperimentStateChangeListener();
}
