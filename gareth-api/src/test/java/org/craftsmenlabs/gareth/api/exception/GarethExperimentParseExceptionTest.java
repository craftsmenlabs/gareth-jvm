package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;


public class GarethExperimentParseExceptionTest {

    private GarethExperimentParseException garethExperimentParseException;

    @Before
    public void setUp() throws Exception {
        garethExperimentParseException = new GarethExperimentParseException();
    }

    @Test
    public void testGetCause() {
        assertNull(garethExperimentParseException.getCause());
    }
}