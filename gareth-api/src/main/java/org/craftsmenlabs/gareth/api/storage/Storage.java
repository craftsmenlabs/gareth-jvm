package org.craftsmenlabs.gareth.api.storage;

import java.util.Optional;

/**
 * Created by hylke on 21/08/15.
 */
public interface Storage {

    /**
     * Get object optional for name
     *
     * @param name
     * @return
     */
    <T extends Object> Optional<Object> get(final String name);

    /**
     * Store object for name
     *
     * @param name
     * @param object
     */
    void store(final String name, final Object object);
}
