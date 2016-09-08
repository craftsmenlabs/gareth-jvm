package org.craftsmenlabs.gareth.json.persist.converter.impl;

import org.craftsmenlabs.gareth.json.persist.converter.TypeConverter;
import org.craftsmenlabs.gareth.json.persist.converter.exception.GarethUnknownTypeConverterException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class DefaultTypeConverterFactoryTest {

    private DefaultTypeConverterFactory defaultTypeConverterFactory;

    @Before
    public void setUp() throws Exception {
        defaultTypeConverterFactory = new DefaultTypeConverterFactory();
    }

    @Test
    public void testCreateTypeConverter() throws Exception {
        final TypeConverter<String> typeConverter = defaultTypeConverterFactory.createTypeConverter(String.class);
        assertTrue(typeConverter instanceof StringTypeConverter);
    }

    @Test
    public void testCreateUnknownTypeConvertForUnknownClass() {
        try {
            defaultTypeConverterFactory.createTypeConverter(Error.class);
            fail("Should not reach this point");
        } catch (final GarethUnknownTypeConverterException e) {
            assertTrue(e.getMessage().contains("Cannot find type converter for class"));
        }
    }


    @Test
    public void testCreateUnknownTypeConvertForArgumentConstructorClass() {
        try {
            defaultTypeConverterFactory
                    .addTypeFactoryForClass(VerifyError.class, ArgumentConstructorTypeConverter.class);
            defaultTypeConverterFactory.createTypeConverter(VerifyError.class);
            fail("Should not reach this point");
        } catch (final GarethUnknownTypeConverterException e) {
            assertTrue(e.getMessage().contains("Cannot initialize type converter"));
        }
    }

    public static class ArgumentConstructorTypeConverter implements TypeConverter<VerifyError> {

        public ArgumentConstructorTypeConverter(final String arg) {
            super();
        }

        @Override
        public String convertToString(VerifyError object) {
            return null;
        }

        @Override
        public VerifyError convertToObject(String stringRepresentation) {
            return null;
        }
    }


}