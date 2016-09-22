package org.craftsmenlabs.gareth.core.observer;

import org.craftsmenlabs.gareth.api.exception.GarethStateWriteException;
import org.craftsmenlabs.gareth.core.persist.listener.ExperimentStateChangeListener;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


public class DefaultObserverTest {

    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Mock
    private ExperimentStateChangeListener experimentStateChangeListener;

    @Mock
    private ExperimentStateChangeListener secondExperimentStateChangeListener;

    private DefaultObserver defaultObserver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        defaultObserver = new DefaultObserver();
        defaultObserver.registerExperimentStateChangeListener(experimentStateChangeListener);
    }

    @Test
    public void testNotifyApplicationStateChanged() throws Exception {
        defaultObserver.notifyApplicationStateChanged(mockExperimentEngine);
        verify(experimentStateChangeListener).onChange(mockExperimentEngine);
    }

    @Test
    public void testNotifyApplicationStateChangedWithException() throws Exception {
        doThrow(GarethStateWriteException.class).when(experimentStateChangeListener).onChange(mockExperimentEngine);

        try {
            defaultObserver.notifyApplicationStateChanged(mockExperimentEngine);
        } catch (final Exception e) {
            fail("Should not reach this point");
        }
    }

    @Test
    public void testRegisterExperimentStateChangeListener() throws Exception {
        defaultObserver.registerExperimentStateChangeListener(secondExperimentStateChangeListener);
        defaultObserver.notifyApplicationStateChanged(mockExperimentEngine);
        verify(experimentStateChangeListener).onChange(mockExperimentEngine);
        verify(secondExperimentStateChangeListener).onChange(mockExperimentEngine);
    }
}