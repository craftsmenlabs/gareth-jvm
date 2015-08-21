package org.craftsmenlabs.gareth.core.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by hylke on 21/08/15.
 */
public class DefaultStorageTest {

    private DefaultStorage defaultStorage;

    @Before
    public void setUp() throws Exception {
        defaultStorage = new DefaultStorage();
        defaultStorage.store("key","value");
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
}