package org.craftsmenlabs.gareth.core.registry;

import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.Duration;

import static org.junit.Assert.*;

/**
 * Created by hylke on 11/08/15.
 */
public class DefinitionRegistryImplTest {

    private DefinitionRegistryImpl definitionRegistry;

    Method mockMethod;

    MethodDescriptor mockMethodDescriptor;

    Duration duration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMethod = this.getClass().getMethod("dummyMethod");
        mockMethodDescriptor = new MethodDescriptorImpl(mockMethod, 0, false);

        duration = Duration.ofHours(2);
        // Add default registries
        definitionRegistry = new DefinitionRegistryImpl();
        definitionRegistry.getBaselineDefinitions().put("baseline", mockMethodDescriptor);
        definitionRegistry.getAssumeDefinitions().put("assume", mockMethodDescriptor);
        definitionRegistry.getSuccessDefinitions().put("success", mockMethodDescriptor);
        definitionRegistry.getFailureDefinitions().put("failure", mockMethodDescriptor);
        definitionRegistry.getTimeDefinitions().put("time", duration);
    }

    @Test
    public void testGetMethodForBaseline() throws Exception {
        assertEquals(mockMethodDescriptor, definitionRegistry.getMethodDescriptorForBaseline("baseline"));
    }

    @Test
    public void testGetMethodForAssume() throws Exception {
        assertEquals(mockMethodDescriptor, definitionRegistry.getMethodDescriptorForAssume("assume"));
    }

    @Test
    public void testGetMethodForSuccess() throws Exception {
        assertEquals(mockMethodDescriptor, definitionRegistry.getMethodDescriptorForSuccess("success"));
    }

    @Test
    public void testGetMethodForFailure() throws Exception {
        assertEquals(mockMethodDescriptor, definitionRegistry.getMethodDescriptorForFailure("failure"));
    }

    @Test
    public void testGetDurationForTime() throws Exception {
        assertEquals(duration, definitionRegistry.getDurationForTime("time"));
    }

    @Test
    public void testGetMethodForBaselineUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodDescriptorForBaseline("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForAssumeUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodDescriptorForAssume("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForSuccessUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodDescriptorForSuccess("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownDefinitionException e) {
            assertEquals("No definition found for glue line 'unknown'", e.getMessage());
        }
    }

    @Test
    public void testGetMethodForFailureUnknownGlueLine() throws Exception {
        try {
            definitionRegistry.getMethodDescriptorForFailure("unknown");
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
        definitionRegistry.addMethodDescriptorForBaseline(glueLine, mockMethodDescriptor);
        assertEquals(2, definitionRegistry.getBaselineDefinitions().size());
        assertTrue(definitionRegistry.getBaselineDefinitions().containsKey(glueLine));
        assertEquals(mockMethodDescriptor, definitionRegistry.getBaselineDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForBaselineWithDuplicateName() throws Exception {
        final String glueLine = "baseline";
        try {
            definitionRegistry.addMethodDescriptorForBaseline(glueLine, mockMethodDescriptor);
            fail("Should not reach this point");
        } catch (final GarethAlreadyKnownDefinitionException e) {
            assertEquals("Glue line already registered for 'baseline'", e.getMessage());
        }
    }

    @Test
    public void testAddMethodForAssume() throws Exception {
        final String glueLine = "assume2";
        definitionRegistry.addMethodDescriptorForAssume(glueLine, mockMethodDescriptor);
        assertEquals(2, definitionRegistry.getAssumeDefinitions().size());
        assertTrue(definitionRegistry.getAssumeDefinitions().containsKey(glueLine));
        assertEquals(mockMethodDescriptor, definitionRegistry.getAssumeDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForAssumeWithDuplicateName() throws Exception {
        final String glueLine = "assume";
        try {
            definitionRegistry.addMethodDescriptorForAssume(glueLine, mockMethodDescriptor);
            fail("Should not reach this point");
        } catch (final GarethAlreadyKnownDefinitionException e) {
            assertEquals("Glue line already registered for 'assume'", e.getMessage());
        }
    }

    @Test
    public void testAddMethodForSuccess() throws Exception {
        final String glueLine = "success2";
        definitionRegistry.addMethodDescriptorForSuccess(glueLine, mockMethodDescriptor);
        assertEquals(2, definitionRegistry.getSuccessDefinitions().size());
        assertTrue(definitionRegistry.getSuccessDefinitions().containsKey(glueLine));
        assertEquals(mockMethodDescriptor, definitionRegistry.getSuccessDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForSuccessWithDuplicateName() throws Exception {
        final String glueLine = "success";
        try {
            definitionRegistry.addMethodDescriptorForSuccess(glueLine, mockMethodDescriptor);
            fail("Should not reach this point");
        } catch (final GarethAlreadyKnownDefinitionException e) {
            assertEquals("Glue line already registered for 'success'", e.getMessage());
        }
    }

    @Test
    public void testAddMethodForFailure() throws Exception {
        final String glueLine = "failure2";
        definitionRegistry.addMethodDescriptorForFailure(glueLine, mockMethodDescriptor);
        assertEquals(2, definitionRegistry.getFailureDefinitions().size());
        assertTrue(definitionRegistry.getFailureDefinitions().containsKey(glueLine));
        assertEquals(mockMethodDescriptor, definitionRegistry.getFailureDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForFailureWithDuplicateName() throws Exception {
        final String glueLine = "success";
        try {
            definitionRegistry.addMethodDescriptorForSuccess(glueLine, mockMethodDescriptor);
            fail("Should not reach this point");
        } catch (final GarethAlreadyKnownDefinitionException e) {
            assertEquals("Glue line already registered for 'success'", e.getMessage());
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