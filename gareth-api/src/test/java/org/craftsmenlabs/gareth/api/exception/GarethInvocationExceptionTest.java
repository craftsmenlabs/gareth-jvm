package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class GarethInvocationExceptionTest {


    private GarethInvocationException garethInvocationException;

    private final Exception illegalArgumentException = new IllegalArgumentException();

    @Before
    public void setUp() throws Exception {
        garethInvocationException = new GarethInvocationException(illegalArgumentException);
    }

    @Test
    public void testGetCause() {
        assertEquals(illegalArgumentException, garethInvocationException.getCause());
    }
}