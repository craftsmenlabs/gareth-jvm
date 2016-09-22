package org.craftsmenlabs.gareth.json.persist.listener;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

public class JsonExperimentChangeListenerTest {

    private JsonExperimentChangeListener jsonExperimentChangeListener;

    @Mock
    private JsonExperimentEnginePersistence mockJsonExperimentEnginePersistence;


    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        jsonExperimentChangeListener = new JsonExperimentChangeListener(mockJsonExperimentEnginePersistence);
    }

    @Test
    public void testOnChange() throws Exception {
        jsonExperimentChangeListener.onChange(mockExperimentEngine);
        verify(mockJsonExperimentEnginePersistence).persist(mockExperimentEngine);
    }

    @Test
    public void testBuildWithNullExperimentEngine() {
        try {
            new JsonExperimentChangeListener(null);
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("File system persistence engine cannot be null"));
        }
    }
}