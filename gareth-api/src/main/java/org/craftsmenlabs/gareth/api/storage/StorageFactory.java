package org.craftsmenlabs.gareth.api.storage;

/**
 * Created by hylke on 21/08/15.
 */
public interface StorageFactory {

    /**
     * Create a new Storage item
     *
     * @return storage item
     */
    Storage createStorage();
}
