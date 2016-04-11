package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hylke on 18/09/15.
 */
public class GarethUnknownExperimentExceptionTest {

    private GarethUnknownExperimentException garethUnknownExperimentException;

    @Before
    public void before() throws Exception {
        garethUnknownExperimentException = new GarethUnknownExperimentException("message");
    }

    @Test
    public void testMessage() {
        assertTrue(garethUnknownExperimentException.getMessage().contains("message"));
    }

    @Test
    public void testCause() {
        assertNull(garethUnknownExperimentException.getCause());
    }

}