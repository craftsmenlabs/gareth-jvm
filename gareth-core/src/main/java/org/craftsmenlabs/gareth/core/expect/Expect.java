package org.craftsmenlabs.gareth.core.expect;

import org.craftsmenlabs.gareth.core.expect.exception.ExpectException;

import java.util.Optional;


public class Expect {

    public static void fail() {
        fail(null);
    }

    /**
     * Fail with message
     *
     * @param message
     */
    public static void fail(final String message) {
        if (Optional.ofNullable(message).isPresent()) {
            throw new ExpectException(message);
        }
        throw new ExpectException();
    }
}
