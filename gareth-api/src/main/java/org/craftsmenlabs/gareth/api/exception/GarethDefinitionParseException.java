package org.craftsmenlabs.gareth.api.exception;


public class GarethDefinitionParseException extends RuntimeException {

    public GarethDefinitionParseException(final Throwable cause) {
        super(cause);
    }

    public GarethDefinitionParseException(String message) {
        super(message);
    }
}
