package org.craftsmenlabs.gareth.core.context;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ExperimentContextImplTest {

    private final String hash = "hash";
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
                .build(hash);

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
                .build(hash);
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidBaselineMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setAssume(stubMethodDescriptor)
                .setTime(mockDuration)
                .build(hash);
        assertFalse(experimentContext.isValid());
    }

    @Test
    public void testIsValidAssumeMissing() throws Exception {
        experimentContext = new ExperimentContextImpl
                .Builder("experiment name", assumptionBlock)
                .setBaseline(stubMethodDescriptor)
                .setTime(mockDuration)
                .build(hash);
        assertFalse(experimentContext.isValid());
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
    public void testGetHash() {
        assertEquals("hash", experimentContext.getHash());
    }

    @Test
    public void testHasStorageNoStorageMethods() {
        assertFalse(experimentContext.hasStorage());
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
                .build(hash);
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
                .build(hash);
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
                .build(hash);
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
                .build(hash);
        assertTrue(experimentContext.hasStorage());
    }


    @Test
    public void testBuildWithoutHash() {
        try {
            new ExperimentContextImpl.Builder("experiment name", assumptionBlock).build(null);
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("ExperimentContext cannot be build without hash"));
        }
    }


    private Optional<MethodDescriptor> stubMethodDescriptor() {
        return Optional.ofNullable(new MethodDescriptorImpl(this.getClass().getMethods()[0], 0, false));
    }
}