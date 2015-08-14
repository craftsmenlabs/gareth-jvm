package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 13/08/15.
 */
public class GarethInvocationException extends RuntimeException {

    public GarethInvocationException(String message) {
        super(message);
    }

    public GarethInvocationException(Throwable cause) {
        super(cause);
    }
}