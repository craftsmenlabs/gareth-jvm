package org.craftsmenlabs.gareth.api.listener;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;

/**
 * Created by hylke on 26/10/15.
 */
public interface ExperimentStateChangeListener {

    /**
     * Perform this action when the experiment engine changes state
     *
     * @param experimentEngine
     */
    void onChange(final ExperimentEngine experimentEngine) throws GarethStateWriteException;

}
