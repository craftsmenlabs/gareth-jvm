package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
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