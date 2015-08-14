package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

        Thread.sleep(3000L);

        assertEquals(2, logItems.size());
        assertEquals("assume", logItems.get(1));
    }

    @Test
    public void testRunExperimentWithSuccess() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-02.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(3000L);

        assertEquals(3, logItems.size());
        assertEquals("assume", logItems.get(1));
        assertEquals("success", logItems.get(2));
    }

    @Test
    public void testRunExperimentWithFailure() throws Exception {
        experimentEngine = new ExperimentEngineImpl
                .Builder(getConfiguration("it-experiment-03.experiment"))
                .build();


        experimentEngine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(3000L);

        assertEquals(2, logItems.size());
        assertEquals("failure", logItems.get(1));
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

    }


}
