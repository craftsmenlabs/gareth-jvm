package org.craftsmenlabs.gareth.core.persist;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class UnknownExperimentContextExceptionTest {

    @Test
    public void testConstructWith() {
        final UnknownExperimentContextException e = new UnknownExperimentContextException("this is a message");
        assertTrue(e.getMessage().contains("this is a message"));
    }

}