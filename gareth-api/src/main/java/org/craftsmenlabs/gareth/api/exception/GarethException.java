package org.craftsmenlabs.gareth.api.exception;

public class GarethException extends RuntimeException {
    public GarethException(String message) {
        super(message);
    }

    public GarethException(Throwable cause) {
        super(cause);
    }

    public GarethException() {
    }
}
