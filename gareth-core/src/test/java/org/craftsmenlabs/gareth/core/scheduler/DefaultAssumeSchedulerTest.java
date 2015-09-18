package org.craftsmenlabs.gareth.core.scheduler;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 18/09/15.
 */
public class DefaultAssumeSchedulerTest {

    private AssumeScheduler defaultAssumeScheduler;

    @Mock
    private ExperimentContext mockExperimentContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        defaultAssumeScheduler = new DefaultAssumeScheduler.Builder().build();
    }

    @Test
    public void testSchedule() throws Exception {
        when(mockExperimentContext.getTime()).thenReturn(Duration.of(1L, ChronoUnit.MILLIS));
        defaultAssumeScheduler.schedule(mockExperimentContext);
    }
}