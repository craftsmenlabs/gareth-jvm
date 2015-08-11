package org.craftsmenlabs.gareth.core.registry;

import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.Temporal;

import static org.junit.Assert.*;

/**
 * Created by hylke on 11/08/15.
 */
public class DefinitionRegistryImplTest {

    private DefinitionRegistryImpl definitionRegistry;

    Method mockMethod;

    Duration duration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMethod = this.getClass().getMethod("dummyMethod");
        duration = Duration.ofHours(2);
        // Add default registries
        definitionRegistry = new DefinitionRegistryImpl();
        definitionRegistry.getBaselineDefinitions().put("baseline", mockMethod);
        definitionRegistry.getAssumeDefinitions().put("assume", mockMethod);
        definitionRegistry.getSuccessDefinitions().put("success", mockMethod);
        definitionRegistry.getFailureDefinitions().put("failure", mockMethod);
        definitionRegistry.getTimeDefinitions().put("time", duration);
    }

    @Test
    public void testGetMethodForBaseline() throws Exception {
        assertEquals(mockMethod, definitionRegistry.getMethodForBaseline("baseline"));
    }

    @Test
    public void testGetMethodForAssume() throws Exception {
        assertEquals(mockMethod, definitionRegistry.getMethodForAssume("assume"));
    }

    @Test
    public void testGetMethodForSuccess() throws Exception {
        assertEquals(mockMethod, definitionRegistry.getMethodForSuccess("success"));
    }

    @Test
    public void testGetMethodForFailure() throws Exception {
        assertEquals(mockMethod, definitionRegistry.getMethodForFailure("failure"));
    }

    @Test
    public void testGetDurationForTime() throws Exception {
        assertEquals(duration, definitionRegistry.getDurationForTime("time"));
    }

    @Test
    public void testGetMethodForBaselineUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodForBaseline("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForAssumeUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodForAssume("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForSuccessUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodForSuccess("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForFailureUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodForFailure("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetDurationForTimeUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getDurationForTime("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testAddMethodForBaseline() throws Exception {
        final String glueLine = "baseline2";
        definitionRegistry.addMethodForBaseline(glueLine, mockMethod);
        assertEquals(2, definitionRegistry.getBaselineDefinitions().size());
        assertTrue(definitionRegistry.getBaselineDefinitions().containsKey(glueLine));
        assertEquals(mockMethod, definitionRegistry.getBaselineDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForBaselineWithDuplicateName() throws Exception {
        final String glueLine = "baseline";
        try {
            definitionRegistry.addMethodForBaseline(glueLine, mockMethod);
            fail("Should not reach this point");
        }catch (final GarethAlreadyKnownDefinitionException e){
            assertEquals("Glue line already registered for 'baseline'",e.getMessage());
        }
    }

    @Test
    public void testAddMethodForAssume() throws Exception {
        final String glueLine = "assume2";
        definitionRegistry.addMethodForAssume(glueLine, mockMethod);
        assertEquals(2, definitionRegistry.getAssumeDefinitions().size());
        assertTrue(definitionRegistry.getAssumeDefinitions().containsKey(glueLine));
        assertEquals(mockMethod, definitionRegistry.getAssumeDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForAssumeWithDuplicateName() throws Exception {
        final String glueLine = "assume";
        try {
            definitionRegistry.addMethodForAssume(glueLine, mockMethod);
            fail("Should not reach this point");
        }catch (final GarethAlreadyKnownDefinitionException e){
            assertEquals("Glue line already registered for 'assume'",e.getMessage());
        }
    }

    @Test
    public void testAddMethodForSuccess() throws Exception {
        final String glueLine = "success2";
        definitionRegistry.addMethodForSuccess(glueLine, mockMethod);
        assertEquals(2, definitionRegistry.getSuccessDefinitions().size());
        assertTrue(definitionRegistry.getSuccessDefinitions().containsKey(glueLine));
        assertEquals(mockMethod, definitionRegistry.getSuccessDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForSuccessWithDuplicateName() throws Exception {
        final String glueLine = "success";
        try {
            definitionRegistry.addMethodForSuccess(glueLine, mockMethod);
            fail("Should not reach this point");
        }catch (final GarethAlreadyKnownDefinitionException e){
            assertEquals("Glue line already registered for 'success'",e.getMessage());
        }
    }

    @Test
    public void testAddMethodForFailure() throws Exception {
        final String glueLine = "failure2";
        definitionRegistry.addMethodForFailure(glueLine, mockMethod);
        assertEquals(2, definitionRegistry.getFailureDefinitions().size());
        assertTrue(definitionRegistry.getFailureDefinitions().containsKey(glueLine));
        assertEquals(mockMethod, definitionRegistry.getFailureDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForFailureWithDuplicateName() throws Exception {
        final String glueLine = "success";
        try {
            definitionRegistry.addMethodForSuccess(glueLine, mockMethod);
            fail("Should not reach this point");
        }catch (final GarethAlreadyKnownDefinitionException e){
            assertEquals("Glue line already registered for 'success'",e.getMessage());
        }
    }


    @Test
    public void testAddDurationForTime() throws Exception {
        final String glueLine = "time2";
        definitionRegistry.addDurationForTime(glueLine, duration);
        assertEquals(2, definitionRegistry.getTimeDefinitions().size());
        assertTrue(definitionRegistry.getTimeDefinitions().containsKey(glueLine));
        assertEquals(duration, definitionRegistry.getTimeDefinitions().get(glueLine));
    }

    public void dummyMethod() {

    }
}