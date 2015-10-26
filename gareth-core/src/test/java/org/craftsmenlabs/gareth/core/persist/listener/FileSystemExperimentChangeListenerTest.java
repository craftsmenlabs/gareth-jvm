package org.craftsmenlabs.gareth.core.persist.listener;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 26/10/15.
 */
public class FileSystemExperimentChangeListenerTest {

    private FileSystemExperimentChangeListener fileSystemExperimentChangeListener;

    @Mock
    private FileSystemExperimentEnginePersistence mockFileSystemExperimentEnginePersistence;

    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fileSystemExperimentChangeListener = new FileSystemExperimentChangeListener.Builder(mockFileSystemExperimentEnginePersistence).build();
    }

    @Test
    public void buildWithoutPersistence() {
        try {
            fileSystemExperimentChangeListener = new FileSystemExperimentChangeListener.Builder(null).build();
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("File system persistence engine cannot be null"));
        }
    }

    @Test
    public void testOnChange() throws Exception {
        fileSystemExperimentChangeListener.onChange(mockExperimentEngine);
        verify(mockFileSystemExperimentEnginePersistence).persist(mockExperimentEngine);
    }


}