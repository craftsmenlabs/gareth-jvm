package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;

/**
 * Created by hylke on 04/11/15.
 */
public class BooleanTypeConverter implements TypeConverter<Boolean> {

    @Override
    public String convertToString(final Boolean object) {
        String value = null;
        if (object != null) {
            value = object.toString();
        }
        return value;
    }

    @Override
    public Boolean convertToObject(final String stringRepresentation) {
        Boolean object = null;
        if (null != stringRepresentation) {
            object = Boolean.valueOf(stringRepresentation);
        }
        return object;
    }
}
