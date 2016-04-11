package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hylke on 18/09/15.
 */
public class GarethInvocationExceptionTest {


    private final Exception illegalArgumentException = new IllegalArgumentException();
    private GarethInvocationException garethInvocationException;

    @Before
    public void setUp() throws Exception {
        garethInvocationException = new GarethInvocationException(illegalArgumentException);
    }

    @Test
    public void testGetCause() {
        assertEquals(illegalArgumentException, garethInvocationException.getCause());
    }
}