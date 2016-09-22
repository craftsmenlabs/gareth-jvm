package org.craftsmenlabs.gareth.json.persist.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class StorageDeserializerTest {

    private StorageDeserializer storageDeserializer;

    @Mock
    private JsonParser mockJsonParser;

    @Mock
    private DeserializationContext mockDeserializationContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        storageDeserializer = new StorageDeserializer();
    }

    @Ignore
    @Test
    public void testDeserialize() throws Exception {
        final DefaultStorage storage = storageDeserializer.deserialize(mockJsonParser, mockDeserializationContext);
    }
}