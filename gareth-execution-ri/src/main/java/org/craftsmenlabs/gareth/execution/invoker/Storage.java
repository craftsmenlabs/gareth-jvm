package org.craftsmenlabs.gareth.execution.invoker;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public interface Storage extends Serializable {
    Optional<? extends Serializable> get(final String name);

    void store(final String name, final Serializable object);

    <T extends Serializable> Optional<T> get(final String name, final Class<T> clazz);

    Set<String> getStorageKeys();

}
