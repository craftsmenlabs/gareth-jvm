package org.craftsmenlabs.gareth.core.scheduler;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class DefaultAssumeSchedulerTest {

    private DefaultAssumeScheduler defaultAssumeScheduler;

    @Mock
    private ExperimentEngineImpl mockExperimentEngine;

    @Mock
    private ExperimentRunContextImpl mockExperimentRunContext;

    @Mock
    private ExperimentContextImpl mockExperimentContext;

    @Mock
    private DefaultObserver mockObserver;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentRunContext.getExperimentContext()).thenReturn(mockExperimentContext);
        defaultAssumeScheduler = new DefaultAssumeScheduler.Builder(mockObserver).build();
    }

    @Test
    public void buildWithoutObserver() {
        try {
            defaultAssumeScheduler = new DefaultAssumeScheduler.Builder(null).build();
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Observer cannot be null"));
        }
    }

    @Test
    public void testSchedule() throws Exception {
        when(mockExperimentContext.getTime()).thenReturn(Duration.of(1L, ChronoUnit.MILLIS));
        defaultAssumeScheduler.schedule(mockExperimentRunContext, mockExperimentEngine);
    }
}