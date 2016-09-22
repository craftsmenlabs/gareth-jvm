package org.craftsmenlabs.gareth.core.persist.listener;

import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;


public interface ExperimentStateChangeListener {

    /**
     * Perform this action when the experiment engine changes state
     *
     * @param experimentEngine
     */
    void onChange(final ExperimentEngineImpl experimentEngine) throws GarethStateWriteException;

}
