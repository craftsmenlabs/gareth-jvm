package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
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