package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class GarethAlreadyKnownDefinitionExceptionTest {


    private GarethAlreadyKnownDefinitionException garethAlreadyKnownDefinitionException;

    @Before
    public void before() throws Exception {
        garethAlreadyKnownDefinitionException = new GarethAlreadyKnownDefinitionException("message");
    }

    @Test
    public void testMessage() {
        assertTrue(garethAlreadyKnownDefinitionException.getMessage().contains("message"));
    }

    @Test
    public void testCause() {
        assertNull(garethAlreadyKnownDefinitionException.getCause());
    }
}