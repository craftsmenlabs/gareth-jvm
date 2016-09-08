package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class GarethAlreadyKnownExperimentExceptionTest {

    private GarethAlreadyKnownExperimentException garethAlreadyKnownExperimentException;

    @Before
    public void before() throws Exception {
        garethAlreadyKnownExperimentException = new GarethAlreadyKnownExperimentException("message");
    }

    @Test
    public void testMessage() {
        assertTrue(garethAlreadyKnownExperimentException.getMessage().contains("message"));
    }

    @Test
    public void testCause() {
        assertNull(garethAlreadyKnownExperimentException.getCause());
    }
}