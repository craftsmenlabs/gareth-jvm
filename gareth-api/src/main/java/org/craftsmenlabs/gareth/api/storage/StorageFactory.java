package org.craftsmenlabs.gareth.api.storage;


public interface StorageFactory {

    /**
     * Create a new Storage item
     *
     * @return storage item
     */
    Storage createStorage();
}
