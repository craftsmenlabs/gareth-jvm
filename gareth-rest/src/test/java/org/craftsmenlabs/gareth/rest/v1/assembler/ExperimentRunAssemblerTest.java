package org.craftsmenlabs.gareth.rest.v1.assembler;

import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.rest.v1.entity.ExperimentRun;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ExperimentRunAssemblerTest {

    private ExperimentRunAssembler experimentRunAssembler;

    @Before
    public void setUp() throws Exception {
        experimentRunAssembler = new ExperimentRunAssembler();
    }

    @Test
    public void testAssembleOutbound() throws Exception {
        final LocalDateTime now = LocalDateTime.now();
        final ExperimentRunContextImpl experimentRunContext = mock(ExperimentRunContextImpl.class);
        when(experimentRunContext.getBaselineState()).thenReturn(ExperimentPartState.OPEN);
        when(experimentRunContext.getAssumeState()).thenReturn(ExperimentPartState.FINISHED);
        when(experimentRunContext.getSuccessState()).thenReturn(ExperimentPartState.NON_EXISTENT);
        when(experimentRunContext.getFailureState()).thenReturn(ExperimentPartState.RUNNING);
        when(experimentRunContext.getBaselineRun()).thenReturn(now);
        when(experimentRunContext.getAssumeRun()).thenReturn(now);
        when(experimentRunContext.getSuccessRun()).thenReturn(now);
        when(experimentRunContext.getFailureRun()).thenReturn(now);

        final ExperimentRun experimentRun = experimentRunAssembler.assembleOutbound(experimentRunContext);
        assertNotNull(experimentRun);

        assertEquals(ExperimentPartState.OPEN.getName(), experimentRun.getBaselineState());
        assertEquals(ExperimentPartState.FINISHED.getName(), experimentRun.getAssumeState());
        assertEquals(ExperimentPartState.NON_EXISTENT.getName(), experimentRun.getSuccessState());
        assertEquals(ExperimentPartState.RUNNING.getName(), experimentRun.getFailureState());

        assertEquals(now, experimentRun.getBaselineExecution());
        assertEquals(now, experimentRun.getAssumeExecution());
        assertEquals(now, experimentRun.getSuccessExecution());
        assertEquals(now, experimentRun.getFailureExecution());
    }


    @Test
    public void testAssembleOutboundWithNull() throws Exception {
        assertNull(experimentRunAssembler.assembleOutbound(null));
    }

    @Test
    public void testAssembleInbound() throws Exception {
        try {
            final ExperimentRun mockExperimentRun = mock(ExperimentRun.class);
            experimentRunAssembler.assembleInbound(mockExperimentRun);
            fail("Should not reach this point");
        } catch (final UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("Experiment run cannot be assembled inbound"));
        }
    }
}