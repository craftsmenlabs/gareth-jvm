package org.craftsmenlabs.gareth.api.exception;


public class GarethStateReadException extends GarethException {

    public GarethStateReadException(final Throwable cause) {
        super(cause);
    }

    public GarethStateReadException(final String message) {
        super(message);
    }
}
