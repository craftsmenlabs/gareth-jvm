package org.craftsmenlabs.gareth.json.persist;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.api.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.json.persist.media.StorageMedia;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class JsonExperimentEnginePersistenceTest {

    private ExperimentEnginePersistence jsonExperimentEnginePersistence;

    @Mock
    private ExperimentEngine mockExperimentEngine;


    @Mock
    private ExperimentContext mockExperimentContext;

    @Mock
    private StorageMedia mockStorageMedia;

    @Mock
    private ExperimentRunContext mockExperimentRunContext;

    @Captor
    private ArgumentCaptor<List<JsonExperimentContextData>> argumentCaptor;


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentEngine.getExperimentRunContexts()).thenReturn(asList(mockExperimentRunContext));
        jsonExperimentEnginePersistence = new JsonExperimentEnginePersistence
                .Builder()
                .setStorageMedia(mockStorageMedia)
                .build();
    }


    @Test
    public void testPersist() throws Exception {
        jsonExperimentEnginePersistence.persist(mockExperimentEngine);
        verify(mockStorageMedia).persist(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().size());
        assertTrue(argumentCaptor.getValue().get(0) instanceof JsonExperimentContextData);
    }

    @Test
    public void testRestore() throws Exception {
        jsonExperimentEnginePersistence.restore(mockExperimentEngine);
        verify(mockStorageMedia).restore();
    }

    @Test
    public void testPersistWithGarethWriteException() throws Exception {
        try {
            doThrow(GarethStateWriteException.class).when(mockStorageMedia).persist(anyList());
            jsonExperimentEnginePersistence.persist(mockExperimentEngine);
            fail("Should not reach this point");
        } catch (final GarethStateWriteException e) {

        }
    }

    @Test
    public void testRestoreWithGarethReadException() throws Exception {
        try {
            when(mockStorageMedia.restore()).thenThrow(GarethStateReadException.class);
            jsonExperimentEnginePersistence.restore(mockExperimentEngine);
            fail("Should not reach this point");
        } catch (final GarethStateReadException e) {

        }
    }

    @Test
    public void testGetExperimentStateChangeListener() throws Exception {
        final ExperimentStateChangeListener experimentStateChangeListener1 = jsonExperimentEnginePersistence
                .getExperimentStateChangeListener();
        final ExperimentStateChangeListener experimentStateChangeListener2 = jsonExperimentEnginePersistence
                .getExperimentStateChangeListener();
        assertSame(experimentStateChangeListener1, experimentStateChangeListener2);
    }


}