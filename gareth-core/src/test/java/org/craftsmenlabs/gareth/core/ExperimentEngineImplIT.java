package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Experiment engine integration test
 * <p>
 * Created by hylke on 14/08/15.
 */
public class ExperimentEngineImplIT {

    private ExperimentEngine experimentEngine;

    private final static List<String> logItems = new ArrayList<>(); // Static because new instance is generated


    @Before
    public void before() {
        logItems.clear();
    }

    @Test
    public void testRunExperiment() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-01.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(200L);

        assertEquals(2, logItems.size());
        assertEquals("assume", logItems.get(1));

        final List<ExperimentRunContext> experimentRunContexts = experimentEngine.getExperimentRunContexts();
        assertEquals(1, experimentRunContexts.size());

        final ExperimentRunContext experimentRunContext = experimentRunContexts.get(0);
        assertTrue(experimentRunContext.isFinished());
        assertNotNull(experimentRunContext.getBaselineRun());
        assertNotNull(experimentRunContext.getAssumeRun());
        assertNull(experimentRunContext.getSuccessRun());
        assertNull(experimentRunContext.getFailureRun());
    }

    @Test
    public void testRunExperimentWithAlreadyHalfRunExperiment() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-01.experiment"))
                .build();


        final ExperimentRunContext mockExperimentRunContext = mock(ExperimentRunContext.class);
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        when(mockExperimentContext.isValid()).thenReturn(true);
        when(mockExperimentRunContext.getHash()).thenReturn("32f5328bb4dada228b7a8a24db729e01cb88425a32cd2f9cfab0ae8953487cda");
        when(mockExperimentRunContext.getBaselineState()).thenReturn(ExperimentPartState.FINISHED);
        when(mockExperimentRunContext.getExperimentContext()).thenReturn(mockExperimentContext);

        experimentEngine.getExperimentRunContexts().add(mockExperimentRunContext);


        experimentEngine.start();

        assertEquals(0, logItems.size());

        Thread.sleep(200L);

        assertEquals(1, logItems.size());
        assertEquals("assume", logItems.get(1));

        final List<ExperimentRunContext> experimentRunContexts = experimentEngine.getExperimentRunContexts();
        assertEquals(1, experimentRunContexts.size());

        final ExperimentRunContext experimentRunContext = experimentRunContexts.get(0);
        assertTrue(experimentRunContext.isFinished());
        assertNotNull(experimentRunContext.getBaselineRun());
        assertNotNull(experimentRunContext.getAssumeRun());
        assertNull(experimentRunContext.getSuccessRun());
        assertNull(experimentRunContext.getFailureRun());
    }

    @Test
    public void testRunExperimentWithSuccess() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-02.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(1000L);

        assertEquals(3, logItems.size());
        assertEquals("assume", logItems.get(1));
        assertEquals("success", logItems.get(2));

        final List<ExperimentRunContext> experimentRunContexts = experimentEngine.getExperimentRunContexts();
        assertEquals(1, experimentRunContexts.size());

        final ExperimentRunContext experimentContext = experimentRunContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNotNull(experimentContext.getAssumeRun());
        assertNotNull(experimentContext.getSuccessRun());
        assertNull(experimentContext.getFailureRun());
    }

    @Test
    public void testRunExperimentWithFailure() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-03.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(1000L);

        assertEquals(2, logItems.size());
        assertEquals("failure", logItems.get(1));


        final List<ExperimentRunContext> experimentContexts = experimentEngine.getExperimentRunContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentRunContext experimentContext = experimentContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNull(experimentContext.getAssumeRun());
        assertNull(experimentContext.getSuccessRun());
        assertNotNull(experimentContext.getFailureRun());
    }

    @Test
    public void testRunExperimentWithStorage() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-04.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline storage", logItems.get(0));

        Thread.sleep(500L);

        assertEquals(2, logItems.size());
        assertEquals("assume storage", logItems.get(1));


        final List<ExperimentRunContext> experimentContexts = experimentEngine.getExperimentRunContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentRunContext experimentContext = experimentContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNotNull(experimentContext.getAssumeRun());
        assertNull(experimentContext.getSuccessRun());
        assertNull(experimentContext.getFailureRun());
    }

    private ExperimentEngineConfig getConfiguration(final String fileName) {
        return new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(ExperimentDefinition.class)
                .addInputStreams(getClass().getResourceAsStream("/it/" + fileName))
                .setIgnoreInvocationExceptions(true)
                .build();

    }


    public class ExperimentDefinition {

        @Baseline(glueLine = "A baseline")
        public void baseline() {
            logItems.add("baseline");
        }

        @Assume(glueLine = "A assumption")
        public void assume() {
            logItems.add("assume");
        }

        @Time(glueLine = "1 day")
        public Duration oneDay() {
            return Duration.of(10, ChronoUnit.MILLIS);
        }

        @Assume(glueLine = "A assumption with failure")
        public void assumeWithFailure() throws Exception {
            throw new Exception("This is a failure");
        }

        @Success(glueLine = "A success")
        public void success() {
            logItems.add("success");
        }

        @Failure(glueLine = "A failure")
        public void failure() {
            logItems.add("failure");
        }

        @Baseline(glueLine = "A baseline with storage")
        public void baseline(final Storage storage) {
            logItems.add("baseline storage");
        }

        @Assume(glueLine = "A assumption with storage")
        public void assume(final Storage storage) {
            logItems.add("assume storage");
        }

    }


}
