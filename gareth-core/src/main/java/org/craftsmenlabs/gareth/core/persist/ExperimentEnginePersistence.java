package org.craftsmenlabs.gareth.core.persist;

import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;


public interface ExperimentEnginePersistence {

    void persist(final ExperimentEngineImpl experimentEngine) throws GarethStateWriteException;

    void restore(final ExperimentEngineImpl experimentEngine) throws GarethStateReadException;

    ExperimentStateChangeListener getExperimentStateChangeListener();
}
