package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;


public class LongTypeConverter implements TypeConverter<Long> {

    @Override
    public String convertToString(final Long object) {
        String value = null;
        if (object != null) {
            value = object.toString();
        }
        return value;
    }

    @Override
    public Long convertToObject(final String stringRepresentation) {
        Long longValue = null;
        if (stringRepresentation != null) {
            longValue = Long.valueOf(stringRepresentation, 10);
        }
        return longValue;
    }
}
