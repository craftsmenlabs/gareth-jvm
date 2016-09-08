package org.craftsmenlabs.gareth.json.persist.converter;

import java.io.Serializable;


public interface TypeConverterFactory {

    /**
     * Get a type converter for class
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Serializable> TypeConverter<T> createTypeConverter(final Class<T> clazz);


    /**
     * Add type converter class for class
     *
     * @param clazz
     * @param typeConverterClass
     * @param <T>
     */
    <T extends Serializable> void addTypeFactoryForClass(final Class<T> clazz, final Class<? extends TypeConverter<T>> typeConverterClass);
}
