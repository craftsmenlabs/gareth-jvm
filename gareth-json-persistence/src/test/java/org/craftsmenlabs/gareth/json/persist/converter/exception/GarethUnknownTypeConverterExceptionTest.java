package org.craftsmenlabs.gareth.json.persist.converter.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 04/11/15.
 */
public class GarethUnknownTypeConverterExceptionTest {


    private GarethUnknownTypeConverterException garethUnknownTypeConverterException;

    @Before
    public void setUp() throws Exception {
        garethUnknownTypeConverterException = new GarethUnknownTypeConverterException("With message");
    }

    @Test
    public void testGetMessage(){
        assertTrue(garethUnknownTypeConverterException.getMessage().contains("With message"));
    }
}