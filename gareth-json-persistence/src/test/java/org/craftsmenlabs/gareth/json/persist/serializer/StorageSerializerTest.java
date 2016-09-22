package org.craftsmenlabs.gareth.json.persist.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.craftsmenlabs.gareth.json.persist.converter.exception.GarethUnknownTypeConverterException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;


public class StorageSerializerTest {

    private StorageSerializer storageSerializer;

    @Mock
    private JsonGenerator mockJsonGenerator;

    @Mock
    private SerializerProvider mockSerializerProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        storageSerializer = new StorageSerializer();
    }

    @Test
    public void testSerialize() throws Exception {

        final DefaultStorage storage = new DefaultStorage();
        storage.store("hello", "world");
        storageSerializer.serialize(storage, mockJsonGenerator, mockSerializerProvider);

        verify(mockJsonGenerator).writeStringField("key", "hello");
        verify(mockJsonGenerator).writeStringField("value", "world");
        verify(mockJsonGenerator).writeStringField("type", "java.lang.String");
    }

    @Test
    public void testSerializeUnsupportedType() throws Exception {

        try {
            final DefaultStorage storage = new DefaultStorage();
            storage.store("hello", new Error());
            storageSerializer.serialize(storage, mockJsonGenerator, mockSerializerProvider);
            fail("Should not reach this point");
        } catch (final GarethUnknownTypeConverterException e) {
            assertTrue(e.getMessage().contains("Cannot find type converter for class"));
        }
    }
}