package org.craftsmenlabs.gareth.core.parser;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ParsedDefinitionFactoryImplTest {

    private ParsedDefinitionFactoryImpl parsedParsedDefinitionFactory;

    @Before
    public void setUp() throws Exception {

        parsedParsedDefinitionFactory = new ParsedDefinitionFactoryImpl(new ReflectionHelper());

    }

    @Test
    public void testParseWithNullClassArgument() throws Exception {
        assertThatThrownBy(() -> parsedParsedDefinitionFactory.parse(null)).hasMessage("Class cannot be null");
    }

    @Test
    public void testParseTimeWithIncorrectReturnType() throws Exception {
        assertThatThrownBy(() -> parsedParsedDefinitionFactory.parse(TimeIncorrectReturnTypeDefinition.class))
                .hasMessage("Method timeDefinition with glue line 'Time glueline' is not a valid method (no duration return type)");
    }

    @Test
    public void testParseTimeWithIncorrectConstructor() throws Exception {
        assertThatThrownBy(() -> parsedParsedDefinitionFactory.parse(TimeIncorrectConstructorDefinition.class))
                .hasMessageContaining("TimeIncorrectConstructorDefinition has no zero argument argument constructor");
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

        final MethodDescriptor methodDescriptor = parsedDefinition.getAssumeDefinitions().get("Assume glueline");
        assertFalse(methodDescriptor.hasStorage());
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
    public void testParseClassWithRegexTime() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(RegexTimeDefinition.class);
        assertNotNull(parsedDefinition);
        assertEquals(1, parsedDefinition.getTimeDefinitions().size());
        assertTrue(parsedDefinition.getTimeDefinitions().containsKey("(\\d+?) ?(\\w*?)"));
    }

    @Test
    public void testParseClassWithBaselineAndStorage() throws Exception {
        final ParsedDefinition parsedDefinition = parsedParsedDefinitionFactory.parse(WithStorageParameter.class);
        assertNotNull(parsedDefinition);

        assertTrue(parsedDefinition.getAssumeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getTimeDefinitions().isEmpty());
        assertTrue(parsedDefinition.getFailureDefinitions().isEmpty());
        assertTrue(parsedDefinition.getSuccessDefinitions().isEmpty());

        assertEquals(1, parsedDefinition.getBaselineDefinitions().size());
        assertTrue(parsedDefinition.getBaselineDefinitions().containsKey("Baseline glueline with storage"));
    }

    @Test
    public void testParseClassWithIncorrectReturnType() {
        assertThatThrownBy(() -> parsedParsedDefinitionFactory.parse(WithIncorrectReturnType.class))
                .hasMessage("Method baselineDefinition with glue line 'Baseline glueline' is not a valid method (no void return type)");
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

    class RegexTimeDefinition {

        public RegexTimeDefinition() {
        }

        @Time(glueLine = "(\\d+?) ?(\\w*?)")
        public Duration timeDefinition(int amount, String unit) {
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

    class WithStorageParameter {

        @Baseline(glueLine = "Baseline glueline with storage")
        public void baselineWithStorageParameter(final Storage storage) {

        }
    }
}