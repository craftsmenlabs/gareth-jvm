package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hylke on 18/09/15.
 */
public class GarethDefinitionParseExceptionTest {

    private final IllegalStateException illegalStateException = new IllegalStateException();
    private GarethDefinitionParseException garethDefinitionParseException;

    @Before
    public void before() throws Exception {
        garethDefinitionParseException = new GarethDefinitionParseException(illegalStateException);
    }

    @Test
    public void testCause() {
        assertEquals(illegalStateException, garethDefinitionParseException.getCause());
    }

}