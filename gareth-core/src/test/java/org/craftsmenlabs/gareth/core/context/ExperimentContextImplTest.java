package org.craftsmenlabs.gareth.core.context;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptorImpl;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by hylke on 17/08/15.
 */
public class ExperimentContextImplTest {

    private ExperimentContext experimentContext;
    private AssumptionBlock assumptionBlock;
    private Duration mockDuration;
    private Optional<MethodDescriptor> stubMethodDescriptor;

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

        stubMethodDescriptor = stubMethodDescriptor();
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setAssume(stubMethodDescriptor)
                .setFailure(stubMethodDescriptor)
                .setSuccess(stubMethodDescriptor)
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
                .setBaseline(stubMethodDescriptor)
                .setAssume(stubMethodDescriptor)
                .build();
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidBaselineMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setAssume(stubMethodDescriptor)
                .setTime(mockDuration)
                .build();
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidAssumeMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
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
        assertEquals(stubMethodDescriptor.get(), experimentContext.getBaseline());
    }

    @Test
    public void testGetAssume() throws Exception {
        assertEquals(stubMethodDescriptor.get(), experimentContext.getAssume());
    }

    @Test
    public void testGetSuccess() throws Exception {
        assertEquals(stubMethodDescriptor.get(), experimentContext.getSuccess());
    }

    @Test
    public void testGetFailure() throws Exception {
        assertEquals(stubMethodDescriptor.get(), experimentContext.getFailure());
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
    public void testBaselineState() {
        assertEquals(ExperimentPartState.OPEN, experimentContext.getBaselineState());
    }

    @Test
    public void testAssumeState() {
        assertEquals(ExperimentPartState.OPEN, experimentContext.getAssumeState());
    }

    @Test
    public void testSuccessState() {
        assertEquals(ExperimentPartState.OPEN, experimentContext.getSuccessState());
    }

    @Test
    public void testFailureState() {
        assertEquals(ExperimentPartState.OPEN, experimentContext.getFailureState());
    }

    @Test
    public void testSetBaselineState() {
        experimentContext.setBaselineState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentContext.getBaselineState());
    }

    @Test
    public void testSetAssumeState() {
        experimentContext.setAssumeState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentContext.getAssumeState());
    }

    @Test
    public void testSetSuccessState() {
        experimentContext.setSuccessState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentContext.getSuccessState());
    }

    @Test
    public void testSetFailureState() {
        experimentContext.setFailureState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentContext.getFailureState());
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

    @Test
    public void testHasStorageNoStorageMethods() {
        assertFalse(experimentContext.hasStorage());
    }

    @Test
    public void testGetDefaultStorage() {
        assertNull(experimentContext.getStorage());
    }

    @Test
    public void testGetWithStorage() {
        final DefaultStorage storage = new DefaultStorage();
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setAssume(stubMethodDescriptor)
                .setFailure(stubMethodDescriptor)
                .setSuccess(stubMethodDescriptor)
                .setTime(mockDuration)
                .setStorage(storage)
                .build();
        assertNotNull(experimentContext.getStorage());
        assertSame(storage, experimentContext.getStorage());
    }

    @Test
    public void testHasStorageOnBaseline() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(Optional.of(new MethodDescriptorImpl(null, 0, true)))
                .setAssume(stubMethodDescriptor)
                .setFailure(stubMethodDescriptor)
                .setSuccess(stubMethodDescriptor)
                .setTime(mockDuration)
                .build();
        assertTrue(experimentContext.hasStorage());
    }

    @Test
    public void testHasStorageOnAssume() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setAssume(Optional.of(new MethodDescriptorImpl(null, 0, true)))
                .setFailure(stubMethodDescriptor)
                .setSuccess(stubMethodDescriptor)
                .setTime(mockDuration)
                .build();
        assertTrue(experimentContext.hasStorage());
    }

    @Test
    public void testHasStorageOnFailure() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setAssume(stubMethodDescriptor)
                .setFailure(Optional.of(new MethodDescriptorImpl(null, 0, true)))
                .setSuccess(stubMethodDescriptor)
                .setTime(mockDuration)
                .build();
        assertTrue(experimentContext.hasStorage());
    }

    @Test
    public void testHasStorageOnSuccess() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setAssume(stubMethodDescriptor)
                .setFailure(stubMethodDescriptor)
                .setSuccess(Optional.of(new MethodDescriptorImpl(null, 0, true)))
                .setTime(mockDuration)
                .build();
        assertTrue(experimentContext.hasStorage());
    }

    @Test
    public void testBuildWithoutMethodDescriptorsAndValidateState() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock).build();
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getBaselineState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getAssumeState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getSuccessState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getFailureState());
    }

    @Test
    public void testBuildWithNullMethodDescriptorsAndValidateState() {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(Optional.ofNullable(null))
                .setFailure(Optional.ofNullable(null))
                .setSuccess(Optional.ofNullable(null))
                .setAssume(Optional.ofNullable(null)).build();
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getBaselineState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getAssumeState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getSuccessState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentContext.getFailureState());
    }


    private Optional<MethodDescriptor> stubMethodDescriptor() {
        return Optional.ofNullable(new MethodDescriptorImpl(this.getClass().getMethods()[0], 0, false));
    }
}