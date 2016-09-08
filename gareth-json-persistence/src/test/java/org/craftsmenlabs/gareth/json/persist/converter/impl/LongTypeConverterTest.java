package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


public class LongTypeConverterTest {

    private LongTypeConverter longTypeConverter;

    @Before
    public void setUp() throws Exception {
        longTypeConverter = new LongTypeConverter();
    }

    @Test
    public void testConvertToString() throws Exception {
        final String value = longTypeConverter.convertToString(10L);
        assertEquals("10", value);
    }

    @Test
    public void testConvertToStringWithNull() throws Exception {
        final String value = longTypeConverter.convertToString(null);
        assertNull(value);
    }

    @Test
    public void testConvertToObject() throws Exception {
        final Long value = longTypeConverter.convertToObject("10");
        assertEquals(new Long(10), value);
    }

    @Test
    public void testConvertToObjectWithNull() throws Exception {
        final Long value = longTypeConverter.convertToObject(null);
        assertNull(value);
    }

    @Test
    public void testConvertToObjectWrittenAsByteCheckRadix() throws Exception {
        final Long value = longTypeConverter.convertToObject("080");
        assertEquals(new Long(80), value);
    }

    @Test
    public void testConvertToObjectWrittenAsByteNotANumber() throws Exception {
        try {
            final Long value = longTypeConverter.convertToObject("fig");
            fail("Should not reach this point");
        } catch (final NumberFormatException e) {

        }
    }
}