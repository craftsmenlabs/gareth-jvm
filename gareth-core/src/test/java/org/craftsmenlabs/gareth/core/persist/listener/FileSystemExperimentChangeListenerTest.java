package org.craftsmenlabs.gareth.core.persist.listener;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class FileSystemExperimentChangeListenerTest {

    private FileSystemExperimentChangeListener fileSystemExperimentChangeListener;

    @Mock
    private FileSystemExperimentEnginePersistence mockFileSystemExperimentEnginePersistence;

    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fileSystemExperimentChangeListener = new FileSystemExperimentChangeListener(mockFileSystemExperimentEnginePersistence);
    }

    @Test
    public void buildWithoutPersistence() {
        try {
            fileSystemExperimentChangeListener = new FileSystemExperimentChangeListener(null);
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
