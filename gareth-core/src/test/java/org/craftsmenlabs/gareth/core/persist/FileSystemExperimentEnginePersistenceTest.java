package org.craftsmenlabs.gareth.core.persist;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 23/09/15.
 */
public class FileSystemExperimentEnginePersistenceTest {


    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Mock
    private ExperimentContext mockExperimentContext;

    private ExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    private File tmpFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        final List<ExperimentContext> experimentContextList = getExperimentContexts();
        when(mockExperimentEngine.getExperimentContexts()).thenReturn(experimentContextList);
        tmpFile = File.createTempFile("gareth", ".state");
        fileSystemExperimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder().setStateFile(tmpFile).build();
    }

    private ArrayList<ExperimentContext> getExperimentContexts() {
        final ArrayList<ExperimentContext> experimentContexts = new ArrayList<>();
        when(mockExperimentContext.getHash()).thenReturn("hash");
        when(mockExperimentContext.getBaselineState()).thenReturn(ExperimentPartState.ERROR);
        when(mockExperimentContext.getAssumeState()).thenReturn(ExperimentPartState.FINISHED);
        when(mockExperimentContext.getSuccessState()).thenReturn(ExperimentPartState.NON_EXISTENT);
        when(mockExperimentContext.getFailureState()).thenReturn(ExperimentPartState.OPEN);
        experimentContexts.add(mockExperimentContext);
        return experimentContexts;
    }

    @After
    public void after() throws Exception {
        tmpFile.deleteOnExit();
        while (!tmpFile.delete()) {
            tmpFile.delete();
        }
    }

    @Test
    public void testPersist() throws Exception {
        fileSystemExperimentEnginePersistence.persist(mockExperimentEngine);
        final List<ExperimentContextData> experimentContextDataList = readContextFromTmpFile(tmpFile);
        assertNotNull(experimentContextDataList);
        assertEquals(1, experimentContextDataList.size());
    }

    @Test
    public void testPersistWithDefaultStateFile() throws Exception {
        fileSystemExperimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder().build();
        fileSystemExperimentEnginePersistence.persist(mockExperimentEngine);
        final List<ExperimentContextData> experimentContextDataList = readContextFromTmpFile(new File(System.getProperty("java.io.tmpdir"), "gareth.state"));
        assertNotNull(experimentContextDataList);
        assertEquals(1, experimentContextDataList.size());
    }

    private List<ExperimentContextData> readContextFromTmpFile(final File tmpFile) {
        List<ExperimentContextData> experimentContextDataList = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(tmpFile);
            ois = new ObjectInputStream(fis);
            experimentContextDataList = (List<ExperimentContextData>) ois.readObject();
        } catch (final Exception e) {
            fail("Should not reach this point");
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return experimentContextDataList;
    }


    @Test
    public void testRestore() throws Exception {
        copyDataToTmpFile();
        fileSystemExperimentEnginePersistence.restore(mockExperimentEngine);
        verify(mockExperimentEngine).getExperimentContexts();
    }

    private void copyDataToTmpFile() throws IOException {
        FileOutputStream output = null;
        InputStream input = null;
        try {
            output = new FileOutputStream(tmpFile);
            input = getClass().getResourceAsStream("/gareth-test.data");
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    @Test
    public void testRestoreWithUnreadableFile() throws Exception {
        try {
            fileSystemExperimentEnginePersistence.restore(mockExperimentEngine);
        } catch (final GarethStateReadException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void testRestoreNonExistingFile() throws Exception {
        try {
            fileSystemExperimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder().setStateFile(new File("/not/present/file")).build();
            fileSystemExperimentEnginePersistence.restore(mockExperimentEngine);
            fail("Should not read this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
}