package org.craftsmenlabs.gareth.api.storage;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


public interface Storage extends Serializable {

    /**
     * Get object optional for name
     *
     * @param name
     * @return
     */
    <T extends Serializable> Optional<? extends Serializable> get(final String name);


    /**
     * Get type based on class
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Serializable> Optional<T> get(final String name, final Class<T> clazz);

    /**
     * Store object for name
     *
     * @param name
     * @param object
     */
    void store(final String name, final Serializable object);

    /**
     * Get a set of storage keys
     *
     * @return
     */
    Set<String> getStorageKeys();
}
