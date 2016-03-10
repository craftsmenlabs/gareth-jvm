package org.craftsmenlabs.gareth.core.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by hylke on 21/08/15.
 */
public class DefaultStorageTest {

    private DefaultStorage defaultStorage;

    @Before
    public void setUp() throws Exception {
        defaultStorage = new DefaultStorage();
        defaultStorage.store("key", "value");
    }


    @Test
    public void testGet() throws Exception {
        final Optional<?> value = defaultStorage.get("key");
        assertTrue(value.isPresent());
        assertEquals("value", value.get());
    }

    @Test
    public void testGetWithUnknownName() throws Exception {
        final Optional<?> value = defaultStorage.get("unknown");
        assertFalse("value", value.isPresent());
    }


    @Test
    public void testGetWithType() {
        final Optional<String> value = defaultStorage.get("key", String.class);
        assertTrue(value.isPresent());
        assertEquals("value", value.get());
    }

    @Test
    public void testGetWithTypeUnknownValue() {
        final Optional<String> value = defaultStorage.get("unknown", String.class);
        assertFalse(value.isPresent());
    }

    @Test
    public void testGetWithTypeWrongType() {
        final Optional<Integer> value = defaultStorage.get("key", Integer.class);
        assertFalse(value.isPresent());
    }

    @Test
    public void testStorageGetKeys() {
        final Set<String> keys = defaultStorage.getStorageKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("key"));
    }
}