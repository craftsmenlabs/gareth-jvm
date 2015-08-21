package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

        final List<ExperimentContext> experimentContexts = experimentEngine.getExperimentContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentContext experimentContext = experimentContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNotNull(experimentContext.getAssumeRun());
        assertNull(experimentContext.getSuccessRun());
        assertNull(experimentContext.getFailureRun());
    }

    @Test
    public void testRunExperimentWithSuccess() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-02.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(500L);

        assertEquals(3, logItems.size());
        assertEquals("assume", logItems.get(1));
        assertEquals("success", logItems.get(2));

        final List<ExperimentContext> experimentContexts = experimentEngine.getExperimentContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentContext experimentContext = experimentContexts.get(0);
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

        Thread.sleep(500L);

        assertEquals(2, logItems.size());
        assertEquals("failure", logItems.get(1));


        final List<ExperimentContext> experimentContexts = experimentEngine.getExperimentContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentContext experimentContext = experimentContexts.get(0);
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


        final List<ExperimentContext> experimentContexts = experimentEngine.getExperimentContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentContext experimentContext = experimentContexts.get(0);
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
