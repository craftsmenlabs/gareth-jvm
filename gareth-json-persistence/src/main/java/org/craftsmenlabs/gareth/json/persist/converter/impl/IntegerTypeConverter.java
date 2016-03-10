package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;

/**
 * Created by hylke on 04/11/15.
 */
public class IntegerTypeConverter implements TypeConverter<Integer> {

    @Override
    public String convertToString(final Integer object) {
        String value = null;
        if (object != null) {
            value = object.toString();
        }
        return value;
    }

    @Override
    public Integer convertToObject(final String stringRepresentation) {
        Integer integer = null;
        if (stringRepresentation != null) {
            integer = Integer.valueOf(stringRepresentation, 10);
        }
        return integer;
    }
}
