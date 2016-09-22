package org.craftsmenlabs.gareth.core.persist.listener;

import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;

public class FileSystemExperimentChangeListener implements ExperimentStateChangeListener {

    private final FileSystemExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    public FileSystemExperimentChangeListener(FileSystemExperimentEnginePersistence fileSystemExperimentEnginePersistence) {
        if (fileSystemExperimentEnginePersistence == null) {
            throw new IllegalStateException("File system persistence engine cannot be null");
        }
        this.fileSystemExperimentEnginePersistence = fileSystemExperimentEnginePersistence;
    }

    @Override
    public void onChange(final ExperimentEngine experimentEngine) throws GarethStateWriteException {
        fileSystemExperimentEnginePersistence.persist(experimentEngine);
    }
}
