package org.craftsmenlabs.gareth.core.storage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;


public class DefaultStorageFactoryTest {

    private DefaultStorageFactory defaultStorageFactory;

    @Before
    public void before() {
        defaultStorageFactory = new DefaultStorageFactory();
    }

    @Test
    public void testCreateStorage() throws Exception {
        final DefaultStorage storage1 = defaultStorageFactory.createStorage();
        final DefaultStorage storage2 = defaultStorageFactory.createStorage();
        assertNotNull(storage1);
        assertNotNull(storage2);
        assertNotSame(storage1, storage2);
    }
}