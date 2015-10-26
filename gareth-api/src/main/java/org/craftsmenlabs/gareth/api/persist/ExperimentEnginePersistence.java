package org.craftsmenlabs.gareth.api.persist;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;

/**
 * Created by hylke on 22/09/15.
 */
public interface ExperimentEnginePersistence {

    void persist(final ExperimentEngine experimentEngine) throws GarethStateWriteException;

    void restore(final ExperimentEngine experimentEngine) throws GarethStateReadException;

    ExperimentStateChangeListener getExperimentStateChangeListener();
}
