package org.craftsmenlabs.gareth.json.persist.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by hylke on 05/11/15.
 */
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
        final Storage storage = storageDeserializer.deserialize(mockJsonParser, mockDeserializationContext);
    }
}