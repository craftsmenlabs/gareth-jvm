package org.craftsmenlabs.gareth.core.storage;

import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.api.storage.StorageFactory;


public class DefaultStorageFactory implements StorageFactory {

    @Override
    public Storage createStorage() {
        return new DefaultStorage();
    }
}
