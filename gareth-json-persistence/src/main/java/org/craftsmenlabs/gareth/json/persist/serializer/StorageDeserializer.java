package org.craftsmenlabs.gareth.json.persist.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;
import org.craftsmenlabs.gareth.json.persist.converter.TypeConverterFactory;
import org.craftsmenlabs.gareth.json.persist.converter.impl.DefaultTypeConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;


public class StorageDeserializer extends JsonDeserializer<Storage> {

    private final static Logger logger = LoggerFactory.getLogger(StorageDeserializer.class);

    private final TypeConverterFactory typeConverterFactory;

    public StorageDeserializer() {
        typeConverterFactory = new DefaultTypeConverterFactory();
    }

    @Override
    public Storage deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final Storage storage = new DefaultStorage();
        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        node.forEach(experimentRunContextNode -> {
            final String key = experimentRunContextNode.get("key").asText();
            final String value = experimentRunContextNode.get("value").asText();
            final String type = experimentRunContextNode.get("type").asText();
            try {
                final TypeConverter typeConverter = typeConverterFactory
                        .createTypeConverter((Class<? extends Serializable>) Class.forName(type));
                final Serializable storedValue = typeConverter.convertToObject(value);
                storage.store(key, storedValue);
            } catch (final Exception e) {
                logger.error("Class cannot be found when deserializing storing value as String", e);
                storage.store(key, value);
            }
        });

        return storage;
    }
}
