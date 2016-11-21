package org.craftsmenlabs.gareth.core;

import com.xeiam.sundial.SundialJobScheduler;
import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.craftsmenlabs.gareth2.ExperimentStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExperimentEngineImplIT {

    private ExperimentEngine engine;
     ExperimentStorage experimentStorage;
    @Before
    public void setUp() throws Exception {
        clearStorage();
    }

    @After
    public void tearDown() throws Exception {
        List<String> allJobNames = SundialJobScheduler.getAllJobNames();
        if (allJobNames != null) {
            allJobNames.forEach(SundialJobScheduler::removeJob);
        }

//        final ArrayList<Experiment> objects = new ArrayList<>();
//        new Verifications(){{
//            final Experiment experiment = withCapture(objects);
//            experimentStorage.save(experiment);
//        }};

        if (engine != null) {
            engine.stop();
        }
    }

    private void clearStorage() {
        File[] files = new File(System.getProperty("java.io.tmpdir")).listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> file.delete());
        }
    }

    @Test
    public void testRunExperiment() throws Exception {
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-01.experiment", new ExperimentDefinition(logItems));

        engine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(200L);

        assertEquals(2, logItems.size());
        assertEquals("assume", logItems.get(1));

        final List<ExperimentRunContext> experimentRunContexts = engine.getExperimentRunContexts();
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
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-01.experiment", new ExperimentDefinition(logItems));

        final ExperimentRunContext mockExperimentRunContext = mock(ExperimentRunContext.class);
        final ExperimentContext mockExperimentContext = mock(ExperimentContext.class);
        when(mockExperimentContext.isValid()).thenReturn(true);
        when(mockExperimentRunContext.getHash())
                .thenReturn("417f839e066b0f1e310268bb89677dac8e64d9621e6362b69b21fa8ca92c05b6");
        when(mockExperimentRunContext.getBaselineState()).thenReturn(ExperimentPartState.FINISHED);
        when(mockExperimentRunContext.getExperimentContext()).thenReturn(mockExperimentContext);

        engine.getExperimentRunContexts().add(mockExperimentRunContext);

        engine.start();

        assertEquals(0, logItems.size());

        Thread.sleep(1000L);

        assertEquals(1, logItems.size());
        assertEquals("assume", logItems.get(1));

        final List<ExperimentRunContext> experimentRunContexts = engine.getExperimentRunContexts();
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
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-02.experiment", new ExperimentDefinition(logItems));

        engine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SS");   // lowercase "dd"
//        System.out.println("############ About to sleep job at " + formatter.format(new GregorianCalendar().getTime()) + ") ###########");
        Thread.sleep(1000L);
//        System.out.println("############ Woke up at " + formatter.format(new GregorianCalendar().getTime()) + ") ###########");

        assertEquals(3, logItems.size());
        assertEquals("assume", logItems.get(1));
        assertEquals("success", logItems.get(2));

        final List<ExperimentRunContext> experimentRunContexts = engine.getExperimentRunContexts();
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
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-03.experiment", new ExperimentDefinition(logItems));

        engine.start();

        assertEquals(1, logItems.size());
        assertEquals("baseline", logItems.get(0));

        Thread.sleep(1000L);

        assertEquals(2, logItems.size());
        assertEquals("failure", logItems.get(1));


        final List<ExperimentRunContext> experimentContexts = engine.getExperimentRunContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentRunContext experimentContext = experimentContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNull(experimentContext.getAssumeRun());
        assertNull(experimentContext.getSuccessRun());
        assertNotNull(experimentContext.getFailureRun());
    }

    @Test
    public void testDefinitionRegistry() {
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-02.experiment", new ExperimentDefinition(logItems));


        engine.start();
        DefinitionRegistry definitions = engine.getDefinitionRegistry();
        Map<GlueLineType, Set<String>> lines = definitions.getGlueLinesPerCategory();
        assertThat(lines).hasSize(5);
        assertThat(lines.get(GlueLineType.BASELINE)).containsExactlyInAnyOrder("A baseline", "^get sale of (.*?)$");
        assertThat(lines.get(GlueLineType.ASSUMPTION))
                .containsExactlyInAnyOrder("An assumption", "^(\\d+) (.*?) were sold$", "An assumption with failure");
        assertThat(lines.get(GlueLineType.SUCCESS)).containsExactlyInAnyOrder("A success");
        assertThat(lines.get(GlueLineType.FAILURE)).containsExactlyInAnyOrder("A failure");
        assertThat(lines.get(GlueLineType.TIME)).containsExactlyInAnyOrder("1 day");
    }

    @Test
    public void testRunExperimentWithStorage() throws Exception {
        List<String> logItems = new ArrayList<>();
        engine = createExperimentEngine("it-experiment-04.experiment", new ExperimentDefinition(logItems));

        engine.start();

        assertEquals(1, logItems.size());
        assertEquals("get sale of carrots", logItems.get(0));

        Thread.sleep(500L);

        assertEquals(2, logItems.size());
        assertEquals("500 carrots were sold", logItems.get(1));


        final List<ExperimentRunContext> experimentContexts = engine.getExperimentRunContexts();
        assertEquals(1, experimentContexts.size());

        final ExperimentRunContext experimentContext = experimentContexts.get(0);
        assertTrue(experimentContext.isFinished());
        assertNotNull(experimentContext.getBaselineRun());
        assertNotNull(experimentContext.getAssumeRun());
        assertNull(experimentContext.getSuccessRun());
        assertNull(experimentContext.getFailureRun());
    }

    private ExperimentEngineConfig getConfiguration(final String fileName) {
        return new ExperimentEngineConfig
                .Builder()
                .addDefinitionClass(ExperimentDefinition.class)
                .addInputStreams(getClass().getResourceAsStream("/it/" + fileName))
                .setIgnoreInvocationExceptions(true)
                .build();

    }


    private ExperimentEngine createExperimentEngine(String fileName, ExperimentDefinition experimentDefinition) {
        return new ExperimentEngineBuilder(getConfiguration(fileName))
                .addCustomDefinitionFactory(clazz -> experimentDefinition)
                .build();
    }

    public class ExperimentDefinition {

        private List<String> logItems;

        public ExperimentDefinition(List<String> logItems) {
            this.logItems = logItems;
        }

        @Baseline(glueLine = "A baseline")
        public void baseline() {
            logItems.add("baseline");
        }

        @Assume(glueLine = "An assumption")
        public void assume() {
            logItems.add("assume");
        }

        @Time(glueLine = "1 day")
        public Duration oneDay() {
            return Duration.of(10, ChronoUnit.MILLIS);
        }

        @Assume(glueLine = "An assumption with failure")
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

        @Baseline(glueLine = "^get sale of (.*?)$")
        public void baseline(final DefaultStorage storage, final String product) {
            logItems.add("get sale of " + product);
        }

        @Assume(glueLine = "^(\\d+) (.*?) were sold$")
        public void assume(final DefaultStorage storage, final int amount, final String product) {
            logItems.add(amount + " " + product + " were sold");
        }
    }
}
