package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;

/**
 * Created by hylke on 04/11/15.
 */
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
