package org.craftsmenlabs.gareth.json.persist.media;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageDeserializer;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageSerializer;

/**
 * Created by hylke on 14/01/16.
 */
public abstract class AbstractStorageMedia {

    public ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Storage.class, new StorageSerializer());
        simpleModule.addDeserializer(Storage.class, new StorageDeserializer());
        //Register modules
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
        objectMapper.setAnnotationIntrospector(introspector);
        return objectMapper;
    }
}
