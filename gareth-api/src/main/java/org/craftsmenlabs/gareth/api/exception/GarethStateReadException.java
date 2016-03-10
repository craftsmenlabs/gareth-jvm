package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 23/09/15.
 */
public class GarethStateReadException extends Exception {

    public GarethStateReadException(final Throwable cause) {
        super(cause);
    }

    public GarethStateReadException(final String message) {
        super(message);
    }
}
