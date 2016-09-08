package org.craftsmenlabs.gareth.api.exception;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


public class GarethStateReadExceptionTest {

    @Test
    public void testConstructWithException() {
        final IllegalStateException wrappedException = new IllegalStateException("e");
        final GarethStateReadException garethStateReadException = new GarethStateReadException(wrappedException);
        assertSame(wrappedException, garethStateReadException.getCause());
    }

    @Test
    public void testConstructWithMessage() {
        final GarethStateReadException garethStateReadException = new GarethStateReadException("message");
        assertSame("message", garethStateReadException.getMessage());
    }


    @Test
    public void testConstructWithoutException() {
        final GarethStateReadException garethStateReadException = new GarethStateReadException((Exception) null);
        assertNull(garethStateReadException.getCause());
    }

    @Test
    public void testConstructWithoutMessage() {
        final GarethStateReadException garethStateReadException = new GarethStateReadException((String) null);
        assertNull(garethStateReadException.getMessage());
    }

}