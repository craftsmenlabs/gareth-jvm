package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 04/08/15.
 */
public class GarethExperimentParseException extends Exception {

    public GarethExperimentParseException() {
        super();
    }

    public GarethExperimentParseException(String message) {
        super(message);
    }

    public GarethExperimentParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GarethExperimentParseException(Throwable cause) {
        super(cause);
    }
}
