package org.craftsmenlabs.gareth.core.persist.listener;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;


public class FileSystemExperimentChangeListener implements ExperimentStateChangeListener {

    private final FileSystemExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    private FileSystemExperimentChangeListener(final Builder builder) {
        this.fileSystemExperimentEnginePersistence = builder.fileSystemExperimentEnginePersistence;
    }

    @Override
    public void onChange(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        fileSystemExperimentEnginePersistence.persist(experimentEngine);
    }

    public static class Builder {

        private final FileSystemExperimentEnginePersistence fileSystemExperimentEnginePersistence;

        public Builder(final FileSystemExperimentEnginePersistence fileSystemExperimentEnginePersistence) {
            this.fileSystemExperimentEnginePersistence = fileSystemExperimentEnginePersistence;
        }

        public FileSystemExperimentChangeListener build() {
            if (fileSystemExperimentEnginePersistence == null) {
                throw new IllegalStateException("File system persistence engine cannot be null");
            }
            return new FileSystemExperimentChangeListener(this);
        }

    }
}
