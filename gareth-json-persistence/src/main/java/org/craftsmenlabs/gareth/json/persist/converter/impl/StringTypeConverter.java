package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;


public class StringTypeConverter implements TypeConverter<String> {

    @Override
    public String convertToString(final String object) {
        return object;
    }

    @Override
    public String convertToObject(final String stringRepresentation) {
        return stringRepresentation;
    }
}
