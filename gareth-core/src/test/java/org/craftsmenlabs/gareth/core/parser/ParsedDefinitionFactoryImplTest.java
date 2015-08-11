package org.craftsmenlabs.gareth.core.parser;

import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseExecption;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * Created by hylke on 10/08/15.
 */
public class ParsedDefinitionFactoryImplTest {


    private ParsedDefinitionFactoryImpl parsedParsedDefinitionFactory;

    @Before
    public void setUp() throws Exception {

        parsedParsedDefinitionFactory = new ParsedDefinitionFactoryImpl();

    }

    @Test
    public void testParseWithNullClassArgument() throws Exception {
        try {
            parsedParsedDefinitionFactory.parse(null);
            fail("Should not reach this point");
        } catch (final IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Class cannot be null");
        }
    }

    @Test
    public void testParseTimeWithIncorrectReturnType() throws Exception {
        try {
            parsedParsedDefinitionFactory.parse(TimeIncorrectReturnTypeDefinition.class);
            fail("Should not reach this point");
        } catch (final IllegalStateException e) {
            assertEquals(e.getMessage(), "java.lang.IllegalStateException: Method timeDefinition with glue line 'Time glueline' is not a valid method (no duration return type)");
        }
    }

    @Test
    public void testParseTimeWithIncorrectConstructor() throws Exception {
        try {
            parsedParsedDefinitionFactory.parse(TimeIncorrectConstructorDefinition.class);
            fail("Should not reach this point");
        } catch (final GarethDefinitionParseExecption e) {
            assertTrue(e.getMessage().contains("TimeIncorrectConstructorDefinition has no zero argument argument constructor"));
        }
    }


    @Test
    public void testParseClassWithoutDefinitions() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(Object.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getBaselineDefinitions().isEmpty());
        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());
    }

    @Test
    public void testParseClassWithAssume() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(AssumeDefinition.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getBaselineDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getAssumeDefinitions().size());
        assertTrue(parsedDefinition.getAssumeDefinitions().containsKey("Assume glueline"));
    }

    @Test
    public void testParseClassWithSuccess() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(SuccessDefinition.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getBaselineDefinitions().isEmpty());
        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getSuccessDefinitions().size());
        assertTrue(parsedDefinition.getSuccessDefinitions().containsKey("Success glueline"));
    }

    @Test
    public void testParseClassWithFailure() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(FailureDefinition.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getBaselineDefinitions().isEmpty());
        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getFailureDefinitions().size());
        assertTrue(parsedDefinition.getFailureDefinitions().containsKey("Failure glueline"));
    }

    @Test
    public void testParseClassWithBaseline() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(BaselineDefinition.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getBaselineDefinitions().size());
        assertTrue(parsedDefinition.getBaselineDefinitions().containsKey("Baseline glueline"));
    }

    @Test
    public void testParseClassWithTime() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(TimeDefinition.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getBaselineDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getTimeDefinitions().size());
        assertTrue(parsedDefinition.getTimeDefinitions().containsKey("Time glueline"));
    }

    @Test
    public void testParseClassWithIncorrectReturnType() throws Exception {
        try {
            parsedParsedDefinitionFactory.parse(WithIncorrectReturnType.class);
            fail("should not reach this point");
        } catch (final IllegalStateException e) {
            assertEquals("java.lang.IllegalStateException: Method baselineDefinition with glue line 'Baseline glueline' is not a valid method (no void return type)"
                    , e.getMessage());
        }
    }

    // classes for testing
    class BaselineDefinition {

        @Baseline(glueLine = "Baseline glueline")
        public void baselineDefinition() {

        }
    }

    class AssumeDefinition {

        @Assume(glueLine = "Assume glueline")
        public void assumeDefinition() {

        }
    }

    class SuccessDefinition {

        @Success(glueLine = "Success glueline")
        public void successDefinition() {

        }
    }

    class FailureDefinition {

        @Failure(glueLine = "Failure glueline")
        public void failureDefinition() {

        }
    }

    class TimeDefinition {

        public TimeDefinition() {
        }

        @Time(glueLine = "Time glueline")
        public Duration timeDefinition() {
            return null;
        }
    }

    class TimeIncorrectConstructorDefinition {
        public TimeIncorrectConstructorDefinition(final String message) {

        }

        @Time(glueLine = "Time glueline")
        public Duration timeDefinition() {
            return null;
        }
    }

    class TimeIncorrectReturnTypeDefinition {

        @Time(glueLine = "Time glueline")
        public String timeDefinition() {
            return null;
        }
    }

    class WithIncorrectReturnType {

        @Baseline(glueLine = "Baseline glueline")
        public Object baselineDefinition() {
            return null;
        }
    }

}