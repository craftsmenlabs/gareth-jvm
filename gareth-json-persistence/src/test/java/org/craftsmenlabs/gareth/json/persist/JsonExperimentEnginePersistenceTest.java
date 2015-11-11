package org.craftsmenlabs.gareth.json.persist;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by hylke on 03/11/15.
 */
public class JsonExperimentEnginePersistenceTest {

    private ExperimentEnginePersistence jsonExperimentEnginePersistence;

    @Mock
    private ExperimentEngine mockExperimentEngine;


    @Mock
    private ExperimentContext mockExperimentContext;

    private File randomTmpFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentEngine.findExperimentContextForHash(anyString())).thenReturn(mockExperimentContext);

        jsonExperimentEnginePersistence = new JsonExperimentEnginePersistence.Builder().build();

        randomTmpFile = File.createTempFile("gareth-random", "state.json");
        randomTmpFile.deleteOnExit();
    }

    @After
    public void after() {
        deleteFileAfterTest(randomTmpFile);
    }

    private void deleteFileAfterTest(final File fileToDelete) {
        while (!fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }

    @Test
    public void testBuildWithStateFile() {
        new JsonExperimentEnginePersistence.Builder().setStateFile(randomTmpFile).build();
        assertTrue(randomTmpFile.exists());
    }

    @Test
    public void testBuildWithCorruptStateFile() {
        try {
            final File corruptFile = mock(File.class);
            when(corruptFile.getPath()).thenReturn("/path");
            when(corruptFile.exists()).thenThrow(IOException.class);
            new JsonExperimentEnginePersistence.Builder().setStateFile(corruptFile).build();
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Cannot setup state file /path"));
        }
    }

    @Test
    public void testPersist() throws Exception {
        jsonExperimentEnginePersistence.persist(mockExperimentEngine);
        verify(mockExperimentEngine).getExperimentRunContexts();
    }

    @Test
    public void testBuildWithNewFile() throws Exception {
        final Random random = new Random();
        final int randomNumber = random.nextInt();
        final File newFile = new File("unknown" + randomNumber);
        newFile.deleteOnExit();
        jsonExperimentEnginePersistence = new JsonExperimentEnginePersistence.Builder().setStateFile(newFile).build();
        assertTrue(newFile.exists());
        deleteFileAfterTest(newFile);
    }

    @Test
    public void testRestore() throws Exception {
        jsonExperimentEnginePersistence = new JsonExperimentEnginePersistence.Builder().setStateFile(randomTmpFile).build();
        copyDataToTmpFile();
        jsonExperimentEnginePersistence.restore(mockExperimentEngine);
        verify(mockExperimentEngine, times(3)).findExperimentContextForHash(anyString());
    }

    @Test
    public void testGetExperimentStateChangeListener() throws Exception {
        final ExperimentStateChangeListener experimentStateChangeListener1 = jsonExperimentEnginePersistence.getExperimentStateChangeListener();
        final ExperimentStateChangeListener experimentStateChangeListener2 = jsonExperimentEnginePersistence.getExperimentStateChangeListener();
        assertSame(experimentStateChangeListener1, experimentStateChangeListener2);
    }

    private void copyDataToTmpFile() throws IOException {
        FileOutputStream output = null;
        InputStream input = null;
        try {
            output = new FileOutputStream(randomTmpFile);
            input = getClass().getResourceAsStream("/sample/test-state-gareth.json");
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }
}