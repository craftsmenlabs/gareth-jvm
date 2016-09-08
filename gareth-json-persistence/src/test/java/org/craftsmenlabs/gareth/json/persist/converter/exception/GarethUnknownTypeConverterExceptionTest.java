package org.craftsmenlabs.gareth.json.persist.converter.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class GarethUnknownTypeConverterExceptionTest {


    private GarethUnknownTypeConverterException garethUnknownTypeConverterException;

    @Before
    public void setUp() throws Exception {
        garethUnknownTypeConverterException = new GarethUnknownTypeConverterException("With message");
    }

    @Test
    public void testGetMessage() {
        assertTrue(garethUnknownTypeConverterException.getMessage().contains("With message"));
    }
}