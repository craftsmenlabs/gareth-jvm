package org.craftsmenlabs.gareth.api.storage;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by hylke on 21/08/15.
 */
public interface Storage extends Serializable {

    /**
     * Get object optional for name
     *
     * @param name
     * @return
     */
    <T extends Serializable> Optional<? extends Serializable> get(final String name);

    /**
     * Store object for name
     *
     * @param name
     * @param object
     */
    void store(final String name, final Serializable object);
}
