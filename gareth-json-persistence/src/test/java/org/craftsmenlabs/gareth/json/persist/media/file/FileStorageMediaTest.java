package org.craftsmenlabs.gareth.json.persist.media.file;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentContextData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FileStorageMediaTest {

    private File randomTmpFile;

    private FileStorageMedia fileStorageMedia;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fileStorageMedia = new FileStorageMedia.Builder().build();

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
        new FileStorageMedia.Builder().setStateFile(randomTmpFile).build();
        assertTrue(randomTmpFile.exists());
    }

    @Test
    public void testBuildWithCorruptStateFile() {
        try {
            final File corruptFile = mock(File.class);
            when(corruptFile.getPath()).thenReturn("/path");
            when(corruptFile.exists()).thenThrow(IOException.class);
            new FileStorageMedia.Builder().setStateFile(corruptFile).build();
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Cannot setup state file /path"));
        }
    }

    @Test
    public void testPersist() throws Exception {
        fileStorageMedia.persist(new ArrayList<>());
        assertTrue(randomTmpFile.getTotalSpace() > 0L);
    }

    @Test
    public void testBuildWithNewFile() throws Exception {
        final Random random = new Random();
        final int randomNumber = random.nextInt();
        final File newFile = new File("unknown" + randomNumber);
        newFile.deleteOnExit();
        fileStorageMedia = new FileStorageMedia.Builder().setStateFile(newFile).build();
        assertTrue(newFile.exists());
        deleteFileAfterTest(newFile);
    }

    @Test
    public void testRestore() throws Exception {
        fileStorageMedia = new FileStorageMedia.Builder().setStateFile(randomTmpFile).build();
        copyDataToTmpFile();
        final List<JsonExperimentContextData> jsonExperimentContextData = fileStorageMedia.restore();
        assertEquals(3, jsonExperimentContextData.size());
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