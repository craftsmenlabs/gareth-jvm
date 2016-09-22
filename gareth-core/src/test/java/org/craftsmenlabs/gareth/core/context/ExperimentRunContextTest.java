package org.craftsmenlabs.gareth.core.context;

import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ExperimentRunContextTest {

    private ExperimentRunContext experimentRunContext;

    @Mock
    private ExperimentContext mockExperimentContext;

    @Mock
    private MethodDescriptor mockMethodDescriptor;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockExperimentContext.getHash()).thenReturn("hash-1");
        when(mockExperimentContext.getBaseline()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getAssume()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getSuccess()).thenReturn(mockMethodDescriptor);
        when(mockExperimentContext.getFailure()).thenReturn(mockMethodDescriptor);
        experimentRunContext = new ExperimentRunContext.Builder(mockExperimentContext, null).build();
    }


    @Test
    public void testGetHash() {
        assertEquals("hash-1", experimentRunContext.getHash());
    }

    @Test
    public void testHasFailuresDefault() throws Exception {
        assertFalse(experimentRunContext.hasFailures());
    }

    @Test
    public void testHasFailuresWithFailure() throws Exception {
        experimentRunContext.setFailureRun(LocalDateTime.now());
        assertTrue(experimentRunContext.hasFailures());
    }

    @Test
    public void testIsRunningDefault() throws Exception {
        assertFalse(experimentRunContext.isRunning());
    }

    @Test
    public void testIsRunningAfterBaseline() throws Exception {
        experimentRunContext.setBaselineRun(LocalDateTime.now());
        assertTrue(experimentRunContext.isRunning());
    }

    @Test
    public void testIsRunningAfterAssume() throws Exception {
        experimentRunContext.setAssumeRun(LocalDateTime.now());
        assertTrue(experimentRunContext.isRunning());
    }

    @Test
    public void testIsRunningAfterFailure() throws Exception {
        experimentRunContext.setFailureRun(LocalDateTime.now());
        assertFalse(experimentRunContext.isRunning());
    }

    @Test
    public void testIsRunningAfterSuccess() throws Exception {
        experimentRunContext.setSuccessRun(LocalDateTime.now());
        assertFalse(experimentRunContext.isRunning());
    }

    @Test
    public void testIsFinishedDefault() throws Exception {
        assertFalse(experimentRunContext.isFinished());
    }

    @Test
    public void testIsFinished() {
        experimentRunContext.setFinished(true);
        assertTrue(experimentRunContext.isFinished());
    }

    @Test
    public void testGetDefaultStates() {
        ExperimentContext mockOtherExperimentContext = mock(ExperimentContext.class);
        experimentRunContext = new ExperimentRunContext.Builder(mockOtherExperimentContext, null).build();
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentRunContext.getBaselineState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentRunContext.getAssumeState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentRunContext.getSuccessState());
        assertEquals(ExperimentPartState.NON_EXISTENT, experimentRunContext.getFailureState());
    }

    @Test
    public void testGetBaselineRunDefault() throws Exception {
        assertNull(experimentRunContext.getBaselineRun());
    }

    @Test
    public void testBaselineState() {
        assertEquals(ExperimentPartState.OPEN, experimentRunContext.getBaselineState());
    }

    @Test
    public void testAssumeState() {
        assertEquals(ExperimentPartState.OPEN, experimentRunContext.getAssumeState());
    }

    @Test
    public void testSuccessState() {
        assertEquals(ExperimentPartState.OPEN, experimentRunContext.getSuccessState());
    }

    @Test
    public void testFailureState() {
        assertEquals(ExperimentPartState.OPEN, experimentRunContext.getFailureState());
    }

    @Test
    public void testSetBaselineState() {
        experimentRunContext.setBaselineState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentRunContext.getBaselineState());
    }

    @Test
    public void testSetAssumeState() {
        experimentRunContext.setAssumeState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentRunContext.getAssumeState());
    }

    @Test
    public void testSetSuccessState() {
        experimentRunContext.setSuccessState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentRunContext.getSuccessState());
    }

    @Test
    public void testSetFailureState() {
        experimentRunContext.setFailureState(ExperimentPartState.FINISHED);
        assertEquals(ExperimentPartState.FINISHED, experimentRunContext.getFailureState());
    }

    @Test
    public void testGetBaselineRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentRunContext.setBaselineRun(runTime);
        assertEquals(runTime, experimentRunContext.getBaselineRun());
    }

    @Test
    public void testGetAssumeRunDefault() throws Exception {
        assertNull(experimentRunContext.getAssumeRun());
    }

    @Test
    public void testGetAssumeRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentRunContext.setAssumeRun(runTime);
        assertEquals(runTime, experimentRunContext.getAssumeRun());
    }

    @Test
    public void testGetSuccessRunDefault() throws Exception {
        assertNull(experimentRunContext.getSuccessRun());
    }

    @Test
    public void testGetSuccessRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentRunContext.setSuccessRun(runTime);
        assertEquals(runTime, experimentRunContext.getSuccessRun());
    }

    @Test
    public void testGetFailureRunDefault() throws Exception {
        assertNull(experimentRunContext.getFailureRun());
    }

    @Test
    public void testGetFailureRun() throws Exception {
        final LocalDateTime runTime = LocalDateTime.now();
        experimentRunContext.setFailureRun(runTime);
        assertEquals(runTime, experimentRunContext.getFailureRun());
    }

    @Test
    public void testGetDefaultStorage() {
        assertNull(experimentRunContext.getStorage());
    }

    @Test
    public void testGetWithStorage() {
        final DefaultStorage storage = new DefaultStorage();
        experimentRunContext = new ExperimentRunContext
                .Builder(mockExperimentContext, storage)
                .build();
        assertNotNull(experimentRunContext.getStorage());
        assertSame(storage, experimentRunContext.getStorage());
    }

    @Test
    public void buildWithoutExperimentContext() {
        try {
            experimentRunContext = new ExperimentRunContext
                    .Builder(null, null)
                    .build();
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("Cannot build experiment run context without experiment context"));
        }
    }
}