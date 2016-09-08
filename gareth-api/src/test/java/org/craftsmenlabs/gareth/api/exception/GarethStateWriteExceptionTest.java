package org.craftsmenlabs.gareth.api.exception;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


public class GarethStateWriteExceptionTest {

    @Test
    public void testConstructWithException() {
        final IllegalStateException wrappedException = new IllegalStateException("e");
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException(wrappedException);
        assertSame(wrappedException, garethStateReadException.getCause());
    }

    @Test
    public void testConstructWithMessage() {
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException("message");
        assertSame("message", garethStateReadException.getMessage());
    }


    @Test
    public void testConstructWithoutException() {
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException((Exception) null);
        assertNull(garethStateReadException.getCause());
    }

    @Test
    public void testConstructWithoutString() {
        final GarethStateWriteException garethStateReadException = new GarethStateWriteException((String) null);
        assertNull(garethStateReadException.getMessage());
    }

}