package org.craftsmenlabs.gareth.api.exception;


public class GarethStateWriteException extends Exception {

    public GarethStateWriteException(final Throwable cause) {
        super(cause);
    }

    public GarethStateWriteException(final String message) {
        super(message);
    }
}
