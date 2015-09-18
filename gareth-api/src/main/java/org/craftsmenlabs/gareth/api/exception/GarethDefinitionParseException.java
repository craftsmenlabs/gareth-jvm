package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 11/08/15.
 */
public class GarethDefinitionParseException extends RuntimeException {

    public GarethDefinitionParseException() {
        super();
    }

    public GarethDefinitionParseException(String message) {
        super(message);
    }

    public GarethDefinitionParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GarethDefinitionParseException(final Throwable cause) {
        super(cause);
    }
}
