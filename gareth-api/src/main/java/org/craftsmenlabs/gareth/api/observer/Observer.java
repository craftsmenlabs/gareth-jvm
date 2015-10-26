package org.craftsmenlabs.gareth.api.observer;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;

/**
 * Created by hylke on 26/10/15.
 */
public interface Observer {

    /**
     * Notify that the application state has changed
     * @param experimentEngine experiment engine that has changed
     */
    void notifyApplicationStateChanged(final ExperimentEngine experimentEngine);

    /**
     * Register a listener for when experiment state has changed
     * @param experimentStateChangeListener
     */
    void registerExperimentStateChangeListener(final ExperimentStateChangeListener experimentStateChangeListener);
}
