package org.craftsmenlabs.gareth.core.storage;

import org.craftsmenlabs.gareth.api.storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by hylke on 21/08/15.
 */
public class DefaultStorage implements Storage {

    private final Map<String, Object> keyValueMap = new HashMap<>();

    @Override
    public Optional<? extends Object> get(final String name) {
        return Optional.ofNullable(keyValueMap.get(name));
    }

    @Override
    public void store(final String name, final Object object) {
        keyValueMap.put(name, object);
    }
}
