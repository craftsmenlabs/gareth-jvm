package org.craftsmenlabs.gareth.core.parser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class ParsedDefinitionImplTest {

    private ParsedDefinitionImpl parsedDefinition;

    @Before
    public void before() {
        parsedDefinition = new ParsedDefinitionImpl();
    }

    @Test
    public void testGetBaselineDefinitions() throws Exception {
        assertNotNull(parsedDefinition.getBaselineDefinitions());
        assertEquals(0, parsedDefinition.getBaselineDefinitions().size());
    }

    @Test
    public void testGetAssumeDefinitions() throws Exception {
        assertNotNull(parsedDefinition.getAssumeDefinitions());
        assertEquals(0, parsedDefinition.getAssumeDefinitions().size());
    }

    @Test
    public void testGetSuccessDefinitions() throws Exception {
        assertNotNull(parsedDefinition.getSuccessDefinitions());
        assertEquals(0, parsedDefinition.getSuccessDefinitions().size());
    }

    @Test
    public void testGetFailureDefinitions() throws Exception {
        assertNotNull(parsedDefinition.getFailureDefinitions());
        assertEquals(0, parsedDefinition.getFailureDefinitions().size());
    }

    @Test
    public void testGetTimeDefinitions() throws Exception {
        assertNotNull(parsedDefinition.getTimeDefinitions());
        assertEquals(0, parsedDefinition.getTimeDefinitions().size());
    }
}