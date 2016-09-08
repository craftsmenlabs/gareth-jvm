package org.craftsmenlabs.gareth.json.persist.converter;

import java.io.Serializable;


public interface TypeConverter<T extends Serializable> {

    /**
     * Convert object to string representation
     *
     * @param object
     * @return
     */
    String convertToString(final T object);


    /**
     * Convert string back to object
     *
     * @param stringRepresentation
     * @return
     */
    T convertToObject(final String stringRepresentation);
}
