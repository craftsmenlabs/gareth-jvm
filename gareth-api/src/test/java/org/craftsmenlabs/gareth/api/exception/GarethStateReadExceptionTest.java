package org.craftsmenlabs.gareth.api.exception;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 24/09/15.
 */
public class GarethStateReadExceptionTest {

    @Test
    public void testConstructWithException() {
        final IllegalStateException wrappedException = new IllegalStateException("e");
        final GarethStateReadException garethStateReadException = new GarethStateReadException(wrappedException);
        assertSame(wrappedException, garethStateReadException.getCause());
    }


    @Test
    public void testConstructWithoutException() {
        final GarethStateReadException garethStateReadException = new GarethStateReadException(null);
        assertNull(garethStateReadException.getCause());
    }

}