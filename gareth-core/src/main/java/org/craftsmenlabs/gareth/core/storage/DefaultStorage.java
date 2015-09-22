package org.craftsmenlabs.gareth.core.storage;

import org.craftsmenlabs.gareth.api.storage.Storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by hylke on 21/08/15.
 */
public class DefaultStorage implements Storage {

    private final Map<String, Serializable> keyValueMap = new HashMap<>();

    @Override
    public Optional<? extends Serializable> get(final String name) {
        return Optional.ofNullable(keyValueMap.get(name));
    }

    @Override
    public void store(final String name, final Serializable object) {
        keyValueMap.put(name, object);
    }
}
