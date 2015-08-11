package org.craftsmenlabs.gareth.api.exception;

/**
 * Created by hylke on 11/08/15.
 */
public class GarethAlreadyKnownExperimentException extends RuntimeException {

    public GarethAlreadyKnownExperimentException(final String message) {
        super(message);
    }
}
