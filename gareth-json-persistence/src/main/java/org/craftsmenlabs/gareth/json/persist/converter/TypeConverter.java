package org.craftsmenlabs.gareth.json.persist.converter;

import java.io.Serializable;

/**
 * Created by hylke on 04/11/15.
 */
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
     * @param stringRepresentation
     * @return
     */
    T convertToObject(final String stringRepresentation);
}
