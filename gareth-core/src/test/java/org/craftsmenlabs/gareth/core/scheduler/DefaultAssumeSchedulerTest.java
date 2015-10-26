package org.craftsmenlabs.gareth.core.scheduler;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.observer.Observer;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 18/09/15.
 */
public class DefaultAssumeSchedulerTest {

    private AssumeScheduler defaultAssumeScheduler;

    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Mock
    private ExperimentRunContext mockExperimentRunContext;

    @Mock
    private ExperimentContext mockExperimentContext;

    @Mock
    private Observer mockObserver;

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