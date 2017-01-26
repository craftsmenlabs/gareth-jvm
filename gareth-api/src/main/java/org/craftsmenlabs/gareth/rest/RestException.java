package org.craftsmenlabs.gareth.rest;


public class RestException extends RuntimeException {

    public RestException(final String message) {
        this(null, message);
    }

    public RestException(final Throwable source, final String message) {
        super(message, source);
    }
}
