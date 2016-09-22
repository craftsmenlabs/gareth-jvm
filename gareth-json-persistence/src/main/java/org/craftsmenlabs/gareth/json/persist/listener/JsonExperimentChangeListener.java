package org.craftsmenlabs.gareth.json.persist.listener;

import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;

public class JsonExperimentChangeListener implements ExperimentStateChangeListener {

    private final JsonExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    public JsonExperimentChangeListener(JsonExperimentEnginePersistence jsonExperimentEnginePersistence) {
        if (jsonExperimentEnginePersistence == null) {
            throw new IllegalStateException("File system persistence engine cannot be null");
        }

        this.fileSystemExperimentEnginePersistence = jsonExperimentEnginePersistence;
    }

    @Override
    public void onChange(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        fileSystemExperimentEnginePersistence.persist(experimentEngine);
    }
}
