package org.craftsmenlabs.gareth.json.persist.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;
import org.craftsmenlabs.gareth.json.persist.converter.TypeConverterFactory;
import org.craftsmenlabs.gareth.json.persist.converter.impl.DefaultTypeConverterFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;


public class StorageSerializer extends JsonSerializer<Storage> {


    private final TypeConverterFactory typeConverterFactory;

    public StorageSerializer() {
        typeConverterFactory = new DefaultTypeConverterFactory();
    }

    @Override
    public void serialize(final Storage storage, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

        if (storage != null) {
            jsonGenerator.writeStartArray();
            for (final String key : storage.getStorageKeys()) {
                final Optional<? extends Serializable> value = storage.get(key);
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("key", key);
                jsonGenerator.writeStringField("type", value.get().getClass().getName());
                jsonGenerator.writeStringField("value", getJsonValue(value));
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
        }
    }


    private String getJsonValue(final Optional<? extends Serializable> value) {
        final Serializable objectValue = value.get();
        final TypeConverter typeConverter = typeConverterFactory.createTypeConverter(objectValue.getClass());
        return typeConverter.convertToString(objectValue);
    }
}
