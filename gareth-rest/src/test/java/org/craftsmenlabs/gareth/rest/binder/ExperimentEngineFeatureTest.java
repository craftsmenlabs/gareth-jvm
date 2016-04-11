package org.craftsmenlabs.gareth.rest.binder;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.FeatureContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by hylke on 18/09/15.
 */
public class ExperimentEngineFeatureTest {

    @Mock
    private ExperimentEngine mockExperimentEngine;

    private ExperimentEngineFeature experimentEngineFeature;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        experimentEngineFeature = new ExperimentEngineFeature(mockExperimentEngine);
    }

    @Test
    public void testConfigure() throws Exception {
        final FeatureContext mockFeatureContext = mock(FeatureContext.class);
        final boolean result = experimentEngineFeature.configure(mockFeatureContext);
        verify(mockFeatureContext).register(any(ExperimentEngineBinder.class));
        assertTrue(result);
    }
}