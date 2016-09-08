package org.craftsmenlabs.gareth.core.persist;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class FileSystemExperimentEnginePersistenceTest {


    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Mock
    private ExperimentRunContext mockExperimentRunContext;

    @Mock
    private ExperimentContext mockExperimentContext;

    private ExperimentEnginePersistence fileSystemExperimentEnginePersistence;

    private File tmpFile;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        final List<ExperimentContext> experimentContextList = getExperimentContexts();
        final List<ExperimentRunContext> experimentRunContextList = getExperimentRunContexts();
        when(mockExperimentRunContext.getExperimentContext()).thenReturn(mockExperimentContext);
        when(mockExperimentEngine.getExperimentContexts()).thenReturn(experimentContextList);
        when(mockExperimentEngine.getExperimentRunContexts()).thenReturn(experimentRunContextList);
        when(mockExperimentEngine.findExperimentContextForHash("hash")).thenReturn(mockExperimentContext);
        tmpFile = File.createTempFile("gareth", ".state");
        fileSystemExperimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
                .setStateFile(tmpFile).build();
    }

    private List<ExperimentContext> getExperimentContexts() {
        final ArrayList<ExperimentContext> experimentContexts = new ArrayList<>();
        when(mockExperimentRunContext.getHash()).thenReturn("hash");
        when(mockExperimentRunContext.getBaselineState()).thenReturn(ExperimentPartState.ERROR);
        when(mockExperimentRunContext.getAssumeState()).thenReturn(ExperimentPartState.FINISHED);
        when(mockExperimentRunContext.getSuccessState()).thenReturn(ExperimentPartState.NON_EXISTENT);
        when(mockExperimentRunContext.getFailureState()).thenReturn(ExperimentPartState.OPEN);
        experimentContexts.add(mockExperimentContext);
        return experimentContexts;
    }


    private List<ExperimentRunContext> getExperimentRunContexts() {
        final ArrayList<ExperimentRunContext> experimentRunContexts = new ArrayList<>();
        experimentRunContexts.add(mockExperimentRunContext);
        return experimentRunContexts;
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
        final List<ExperimentContextData> experimentContextDataList = readContextFromTmpFile(new File(System
                .getProperty("java.io.tmpdir"), "gareth.state"));
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
        verify(mockExperimentEngine).getExperimentRunContexts();
    }

    @Test
    public void testGetExperimentStateChangeListener() {
        final ExperimentStateChangeListener experimentStateChangeListener1 = fileSystemExperimentEnginePersistence
                .getExperimentStateChangeListener();
        final ExperimentStateChangeListener experimentStateChangeListener2 = fileSystemExperimentEnginePersistence
                .getExperimentStateChangeListener();
        assertNotNull(experimentStateChangeListener1);
        assertNotNull(experimentStateChangeListener2);
        assertSame(experimentStateChangeListener1, experimentStateChangeListener2);
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
            fileSystemExperimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
                    .setStateFile(new File("/not/present/file")).build();
            fileSystemExperimentEnginePersistence.restore(mockExperimentEngine);
            fail("Should not read this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
}