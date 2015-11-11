package org.craftsmenlabs.gareth.json.persist.listener;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;

/**
 * Created by hylke on 31/10/15.
 */
public class JsonExperimentChangeListener implements ExperimentStateChangeListener {

    private final JsonExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    private JsonExperimentChangeListener(final Builder builder) {
        this.fileSystemExperimentEnginePersistence = builder.jsonExperimentEnginePersistence;
    }

    @Override
    public void onChange(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        fileSystemExperimentEnginePersistence.persist(experimentEngine);
    }

    public static class Builder {

        private final JsonExperimentEnginePersistence jsonExperimentEnginePersistence;

        public Builder(final JsonExperimentEnginePersistence jsonExperimentEnginePersistence) {
            this.jsonExperimentEnginePersistence = jsonExperimentEnginePersistence;
        }

        public JsonExperimentChangeListener build() {
            if (jsonExperimentEnginePersistence == null) {
                throw new IllegalStateException("File system persistence engine cannot be null");
            }
            return new JsonExperimentChangeListener(this);
        }

    }
}
