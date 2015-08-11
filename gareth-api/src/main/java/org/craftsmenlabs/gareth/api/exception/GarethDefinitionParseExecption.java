package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 11/08/15.
 */
public class GarethDefinitionParseExecption extends RuntimeException {

    public GarethDefinitionParseExecption() {
        super();
    }

    public GarethDefinitionParseExecption(String message) {
        super(message);
    }

    public GarethDefinitionParseExecption(String message, Throwable cause) {
        super(message, cause);
    }

    public GarethDefinitionParseExecption(Throwable cause) {
        super(cause);
    }
}
