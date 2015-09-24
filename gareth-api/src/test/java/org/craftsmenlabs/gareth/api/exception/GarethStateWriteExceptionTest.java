package org.craftsmenlabs.gareth.api.exception;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 24/09/15.
 */
public class GarethStateWriteExceptionTest {

    @Test
    public void testConstructWithException() {
        final IllegalStateException wrappedException = new IllegalStateException("e");
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException(wrappedException);
        assertSame(wrappedException, garethStateReadException.getCause());
    }


    @Test
    public void testConstructWithoutException() {
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException(null);
        assertNull(garethStateReadException.getCause());
    }

}