package org.craftsmenlabs.gareth.rest.v1.assembler;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.rest.v1.entity.Experiment;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hylke on 28/08/15.
 */
public class ExperimentAssemblerTest {

    private ExperimentAssembler experimentAssembler;

    @Before
    public void setUp() throws Exception {
        experimentAssembler = new ExperimentAssembler();
    }

    @Test
    public void testAssembleOutbound() throws Exception {

        final ExperimentContext experimentContext = mock(ExperimentContext.class);
        when(experimentContext.getExperimentName()).thenReturn("experiment");
        when(experimentContext.getBaselineGlueLine()).thenReturn("baseline");
        when(experimentContext.getAssumeGlueLine()).thenReturn("assume");
        when(experimentContext.getTimeGlueLine()).thenReturn("time");
        when(experimentContext.getSuccessGlueLine()).thenReturn("success");
        when(experimentContext.getFailureGlueLine()).thenReturn("failure");
        // Set time
        when(experimentContext.getBaselineState()).thenReturn(ExperimentPartState.OPEN);
        when(experimentContext.getAssumeState()).thenReturn(ExperimentPartState.OPEN);
        when(experimentContext.getSuccessState()).thenReturn(ExperimentPartState.OPEN);
        when(experimentContext.getFailureState()).thenReturn(ExperimentPartState.OPEN);
        // Set execution runs
        final LocalDateTime localDateTime = LocalDateTime.now();
        when(experimentContext.getBaselineRun()).thenReturn(localDateTime);
        when(experimentContext.getAssumeRun()).thenReturn(localDateTime);
        when(experimentContext.getSuccessRun()).thenReturn(localDateTime);
        when(experimentContext.getFailureRun()).thenReturn(localDateTime);

        final Experiment experiment = experimentAssembler.assembleOutbound(experimentContext);
        assertNotNull(experiment);
        assertEquals("experiment", experiment.getExperimentName());
        assertEquals("baseline", experiment.getBaselineGlueLine());
        assertEquals("assume", experiment.getAssumeGlueLine());
        assertEquals("time", experiment.getTimeGlueLine());
        assertEquals("success", experiment.getSuccessGlueLine());
        assertEquals("failure", experiment.getFailureGlueLine());
        assertEquals(localDateTime, experiment.getBaselineExecution());
        assertEquals(localDateTime, experiment.getAssumeExecution());
        assertEquals(localDateTime, experiment.getSuccessExecution());
        assertEquals(localDateTime, experiment.getFailureExecution());
        assertEquals("open", experiment.getBaselineState());
        assertEquals("open", experiment.getAssumeState());
        assertEquals("open", experiment.getSuccessState());
        assertEquals("open", experiment.getFailureState());
    }

    @Test
    public void testAssembleOutboundWithNull() {
        assertNull(experimentAssembler.assembleOutbound(null));
    }


    @Test
    public void testAssembleInbound() throws Exception {
        try {
            final Experiment experiment = mock(Experiment.class);
            experimentAssembler.assembleInbound(experiment);
            fail("should not reach this point");
        } catch (final UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("Experiment cannot be assembled inbound"));
        }
    }
}