package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hylke on 04/11/15.
 */
public class BooleanTypeConverterTest {

    private BooleanTypeConverter booleanTypeConverter;

    @Before
    public void setUp() throws Exception {
        booleanTypeConverter = new BooleanTypeConverter();
    }

    @Test
    public void testConvertToStringWithNull() throws Exception {
        assertNull(booleanTypeConverter.convertToString(null));
    }

    @Test
    public void testConvertToObjectWithNull() throws Exception {
        assertNull(booleanTypeConverter.convertToObject(null));
    }

    @Test
    public void testConvertToStringAsTrue() throws Exception {
        final String value = booleanTypeConverter.convertToString(Boolean.TRUE);
        assertEquals("true", value);
    }

    @Test
    public void testConvertToStringAsFalse() throws Exception {
        final String value = booleanTypeConverter.convertToString(Boolean.FALSE);
        assertEquals("false", value);
    }

    @Test
    public void testConvertToObjectAsTrue() throws Exception {
        final Boolean value = booleanTypeConverter.convertToObject("true");
        assertTrue(value);
    }

    @Test
    public void testConvertToObjectAsFalse() throws Exception {
        final Boolean value = booleanTypeConverter.convertToObject("false");
        assertFalse(value);
    }

    @Test
    public void testConvertToObjectAsOtherValue() throws Exception {
        final Boolean value = booleanTypeConverter.convertToObject("xxds");
        assertFalse(value);
    }
}