package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 04/11/15.
 */
public class IntegerTypeConverterTest {

    private IntegerTypeConverter integerTypeConverter;

    @Before
    public void setUp() throws Exception {
        integerTypeConverter = new IntegerTypeConverter();
    }

    @Test
    public void testConvertToString() throws Exception {
        final String value = integerTypeConverter.convertToString(10);
        assertEquals("10", value);
    }

    @Test
    public void testConvertToStringWithNull() throws Exception {
        final String value = integerTypeConverter.convertToString(null);
        assertNull(value);
    }

    @Test
    public void testConvertToObject() throws Exception {
        final Integer value = integerTypeConverter.convertToObject("10");
        assertEquals(new Integer(10), value);
    }

    @Test
    public void testConvertToObjectWithNull() throws Exception {
        final Integer value = integerTypeConverter.convertToObject(null);
        assertNull(value);
    }

    @Test
    public void testConvertToObjectWrittenAsByteCheckRadix() throws Exception {
        final Integer value = integerTypeConverter.convertToObject("080");
        assertEquals(new Integer(80), value);
    }

    @Test
    public void testConvertToObjectWrittenAsByteNotANumber() throws Exception {
        try {
            final Integer value = integerTypeConverter.convertToObject("fig");
            fail("Should not reach this point");
        }catch(final NumberFormatException e){

        }
    }
}