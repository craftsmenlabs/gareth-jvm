package org.craftsmenlabs.gareth.json.persist.media;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageDeserializer;
import org.craftsmenlabs.gareth.json.persist.serializer.StorageSerializer;


public abstract class AbstractStorageMedia {

    public ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(DefaultStorage.class, new StorageSerializer());
        simpleModule.addDeserializer(DefaultStorage.class, new StorageDeserializer());
        //Register modules
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
        objectMapper.setAnnotationIntrospector(introspector);
        return objectMapper;
    }
}
