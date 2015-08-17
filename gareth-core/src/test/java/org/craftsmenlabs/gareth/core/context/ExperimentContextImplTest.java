package org.craftsmenlabs.gareth.core.context;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

/**
 * Created by hylke on 17/08/15.
 */
public class ExperimentContextImplTest {

    private ExperimentContext experimentContext;
    private AssumptionBlock assumptionBlock;
    private Duration mockDuration;
    private Method stubMethod;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mockDuration = Duration.of(1L, ChronoUnit.SECONDS);

        assumptionBlock = new AssumptionBlock();
        assumptionBlock.setBaseline("baseline");
        assumptionBlock.setAssumption("assume");
        assumptionBlock.setFailure("failure");
        assumptionBlock.setSuccess("success");
        assumptionBlock.setTime("time");

        stubMethod = stubMethod();
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethod)
                .setAssume(stubMethod)
                .setFailure(stubMethod)
                .setSuccess(stubMethod)
                .setTime(mockDuration)
                .build();

    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(experimentContext.isValid());
    }

    @Test
    public void testIsValidTimeMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethod)
                .setAssume(stubMethod)
                .build();
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidBaselineMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setAssume(stubMethod)
                .setTime(mockDuration)
                .build();
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidAssumeMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethod)
                .setTime(mockDuration)
                .build();
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testHasFailuresDefault() throws Exception {
        assertFalse(experimentContext.hasFailures());
    }

    @Test
    public void testHasFailuresWithFailure() throws Exception {
        experimentContext.setFailureRun(LocalDateTime.now());
        assertTrue(experimentContext.hasFailures());
    }

    @Test
    public void testIsRunningDefault() throws Exception {
        assertFalse(experimentContext.isRunning());
    }

    @Test
    public void testIsRunningAfterBaseline() throws Exception {
        experimentContext.setBaselineRun(LocalDateTime.now());
        assertTrue(experimentContext.isRunning());
    }

    @Test
    public void testIsRunningAfterAssume() throws Exception {
        experimentContext.setAssumeRun(LocalDateTime.now());
        assertTrue(experimentContext.isRunning());
    }

    @Test
    public void testIsRunningAfterFailure() throws Exception {
        experimentContext.setFailureRun(LocalDateTime.now());
        assertFalse(experimentContext.isRunning());
    }

    @Test
    public void testIsRunningAfterSuccess() throws Exception {
        experimentContext.setSuccessRun(LocalDateTime.now());
        assertFalse(experimentContext.isRunning());
    }

    @Test
    public void testIsFinishedDefault() throws Exception {
        assertFalse(experimentContext.isFinished());
    }

    @Test
    public void testIsFinished() {
        experimentContext.setFinished(true);
        assertTrue(experimentContext.isFinished());
    }

    @Test
    public void testGetExperimentName() throws Exception {
        assertEquals("experiment name", experimentContext.getExperimentName());
    }

    @Test
    public void testGetBaseline() throws Exception {
        assertEquals(stubMethod, experimentContext.getBaseline());
    }

    @Test
    public void testGetAssume() throws Exception {
        assertEquals(stubMethod, experimentContext.getAssume());
    }

    @Test
    public void testGetSuccess() throws Exception {
        assertEquals(stubMethod, experimentContext.getSuccess());
    }

    @Test
    public void testGetFailure() throws Exception {
        assertEquals(stubMethod, experimentContext.getFailure());
    }

    @Test
    public void testGetTime() throws Exception {
        assertEquals(mockDuration, experimentContext.getTime());
    }

    @Test
    public void testGetBaselineGlueLine() throws Exception {
        assertEquals("baseline", experimentContext.getBaselineGlueLine());
    }

    @Test
    public void testGetAssumeGlueLine() throws Exception {
        assertEquals("assume", experimentContext.getAssumeGlueLine());
    }

    @Test
    public void testGetSuccessGlueLine() throws Exception {
        assertEquals("success", experimentContext.getSuccessGlueLine());
    }

    @Test
    public void testGetFailureGlueLine() throws Exception {
        assertEquals("failure", experimentContext.getFailureGlueLine());
    }

    @Test
    public void testGetTimeGlueLine() throws Exception {
        assertEquals("time", experimentContext.getTimeGlueLine());
    }

    @Test
    public void testGetBaselineRunDefault() throws Exception {
        assertNull(experimentContext.getBaselineRun());
    }

    @Test
    public void testGetBaselineRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentContext.setBaselineRun(runTime);
        assertEquals(runTime, experimentContext.getBaselineRun());
    }

    @Test
    public void testGetAssumeRunDefault() throws Exception {
        assertNull(experimentContext.getAssumeRun());
    }

    @Test
    public void testGetAssumeRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentContext.setAssumeRun(runTime);
        assertEquals(runTime, experimentContext.getAssumeRun());
    }

    @Test
    public void testGetSuccessRunDefault() throws Exception {
        assertNull(experimentContext.getSuccessRun());
    }

    @Test
    public void testGetSuccessRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentContext.setSuccessRun(runTime);
        assertEquals(runTime, experimentContext.getSuccessRun());
    }

    @Test
    public void testGetFailureRunDefault() throws Exception {
        assertNull(experimentContext.getFailureRun());
    }

    @Test
    public void testGetFailureRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentContext.setFailureRun(runTime);
        assertEquals(runTime, experimentContext.getFailureRun());
    }

    private Method stubMethod() {
        return this.getClass().getMethods()[0];
    }
}