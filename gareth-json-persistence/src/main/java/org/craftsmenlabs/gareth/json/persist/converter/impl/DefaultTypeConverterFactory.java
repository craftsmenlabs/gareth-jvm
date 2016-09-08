package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;
import org.craftsmenlabs.gareth.json.persist.converter.TypeConverterFactory;
import org.craftsmenlabs.gareth.json.persist.converter.exception.GarethUnknownTypeConverterException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class DefaultTypeConverterFactory implements TypeConverterFactory {

    private final Map<Class<? extends Serializable>, Class<? extends TypeConverter<? extends Serializable>>> typeFactoryMap = new HashMap<>();

    public DefaultTypeConverterFactory() {
        typeFactoryMap.put(String.class, StringTypeConverter.class);
        typeFactoryMap.put(Integer.class, IntegerTypeConverter.class);
        typeFactoryMap.put(Integer.TYPE, IntegerTypeConverter.class);
        typeFactoryMap.put(Long.class, LongTypeConverter.class);
        typeFactoryMap.put(Long.TYPE, LongTypeConverter.class);
        typeFactoryMap.put(Boolean.TYPE, BooleanTypeConverter.class);
        typeFactoryMap.put(Boolean.class, BooleanTypeConverter.class);
    }

    public <T extends Serializable> void addTypeFactoryForClass(final Class<T> clazz, final Class<? extends TypeConverter<T>> typeConverterClass) {
        typeFactoryMap.put(clazz, typeConverterClass);
    }

    @Override
    public <T extends Serializable> TypeConverter<T> createTypeConverter(final Class<T> clazz) {
        final Class<? extends TypeConverter> typeConverterClass = typeFactoryMap.get(clazz);
        TypeConverter typeConverter = null;
        if (typeConverterClass == null) {
            throw new GarethUnknownTypeConverterException(String
                    .format("Cannot find type converter for class %s", clazz));
        }
        try {
            typeConverter = typeConverterClass.newInstance();

        } catch (final InstantiationException | IllegalAccessException e) {
            throw new GarethUnknownTypeConverterException("Cannot initialize type converter");
        }

        return typeConverter;

    }
}
