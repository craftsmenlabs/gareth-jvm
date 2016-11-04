package org.craftsmenlabs.gareth.execution.invoker;

import java.io.Serializable;
import java.util.*;

public class DefaultStorage implements Storage {

    private final Map<String, Serializable> keyValueMap = new HashMap<>();

    public Optional<? extends Serializable> get(final String name) {
        return Optional.ofNullable(keyValueMap.get(name));
    }

    public void store(final String name, final Serializable object) {
        keyValueMap.put(name, object);
    }

    public <T extends Serializable> Optional<T> get(final String name, final Class<T> clazz) {
        final Optional bla = get(name);
        Optional<T> optional = Optional.empty();
        if (bla.isPresent() && bla.get().getClass().equals(clazz)) {
            optional = Optional.of(clazz.cast(bla.get()));
        }
        return optional;
    }

    public Set<String> getStorageKeys() {
        return keyValueMap.keySet();
    }
}
