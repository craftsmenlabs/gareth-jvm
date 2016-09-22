package org.craftsmenlabs.gareth.core.observer;

import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class DefaultObserver {

    private final static Logger logger = LoggerFactory.getLogger(DefaultObserver.class);

    private final List<ExperimentStateChangeListener> experimentStateChangeListenerList = new ArrayList<>();

    public void notifyApplicationStateChanged(final ExperimentEngine experimentEngine) {
        logger.debug("Notifying the application state listeners on change");
        for (final ExperimentStateChangeListener experimentStateChangeListener : experimentStateChangeListenerList) {
            try {
                experimentStateChangeListener.onChange(experimentEngine);
            } catch (final GarethStateWriteException e) {
                logger.error("Error while persisting experiment engine state");
            }
        }
    }

    public void registerExperimentStateChangeListener(final ExperimentStateChangeListener experimentStateChangeListener) {
        experimentStateChangeListenerList.add(experimentStateChangeListener);
    }
}
