package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 04/11/15.
 */
public class StringTypeConverterTest {

    private StringTypeConverter stringTypeConverter;

    @Before
    public void setUp() throws Exception {
        stringTypeConverter = new StringTypeConverter();
    }

    @Test
    public void testConvertToString() throws Exception {
        final String value = stringTypeConverter.convertToString("bla");
        assertEquals("bla",value);
    }

    @Test
    public void testConvertToObject() throws Exception {
        final String value = stringTypeConverter.convertToObject("foo");
        assertEquals("foo",value);
    }

    @Test
    public void testConvertToStringWithNull() throws Exception {
        assertNull(stringTypeConverter.convertToString(null));
    }

    @Test
    public void testConvertToObjectWithNull() throws Exception {
        assertNull(stringTypeConverter.convertToObject(null));
    }
}