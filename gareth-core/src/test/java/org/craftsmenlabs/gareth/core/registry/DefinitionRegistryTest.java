package org.craftsmenlabs.gareth.core.registry;

import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefinitionRegistryTest {

    private Method mockMethod;
    private MethodDescriptor baseLineDescriptor;
    private MethodDescriptor baseLineDescriptor2;
    private MethodDescriptor assumeDescriptor;
    private MethodDescriptor successDescriptor;
    private MethodDescriptor failureDescriptor;
    private Duration duration;
    private DefinitionRegistry registry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMethod = this.getClass().getMethod("dummyMethod");
        baseLineDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "baseline");
        baseLineDescriptor2 = new MethodDescriptorImpl(mockMethod, 0, false, "baseline2");
        assumeDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "assume");
        successDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "success");
        failureDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "failure");

        duration = Duration.ofHours(2);
        // Add default registries
        registry = new DefinitionRegistry();
        registry.getBaselineDefinitions().put("baseline", baseLineDescriptor);
        registry.getAssumeDefinitions().put("assume", assumeDescriptor);
        registry.getSuccessDefinitions().put("success", successDescriptor);
        registry.getFailureDefinitions().put("failure", failureDescriptor);
        registry.getTimeDefinitions().put("time", duration);
    }

    @Test
    public void testGetAllGlueLinesPerCategory() {
        registry.getBaselineDefinitions().put("baseline2", baseLineDescriptor2);
        Map<GlueLineType, Set<String>> allPatterns = registry.getGlueLinesPerCategory();
        assertThat(allPatterns).hasSize(5);
        assertThat(allPatterns.get(GlueLineType.BASELINE)).containsExactlyInAnyOrder("baseline", "baseline2");
        assertThat(allPatterns.get(GlueLineType.ASSUMPTION)).containsExactly("assume");
        assertThat(allPatterns.get(GlueLineType.SUCCESS)).containsExactly("success");
        assertThat(allPatterns.get(GlueLineType.FAILURE)).containsExactly("failure");
        assertThat(allPatterns.get(GlueLineType.TIME)).containsExactly("time");
    }

    @Test
    public void testGetMethodForBaseline() throws Exception {
        assertEquals(baseLineDescriptor, registry.getMethodDescriptorForBaseline("baseline"));
    }

    @Test
    public void testGetMethodForAssume() throws Exception {
        assertEquals(assumeDescriptor, registry.getMethodDescriptorForAssume("assume"));
    }

    @Test
    public void testGetMethodForSuccess() throws Exception {
        assertEquals(successDescriptor, registry.getMethodDescriptorForSuccess("success"));
    }

    @Test
    public void testGetMethodForFailure() throws Exception {
        assertEquals(failureDescriptor, registry.getMethodDescriptorForFailure("failure"));
    }

    @Test
    public void testGetDurationForTime() throws Exception {
        assertEquals(duration, registry.getDurationForTime("time"));
    }

    @Test
    public void testGetMethodForBaselineUnknownGlueLine() throws Exception {
        assertThatThrownBy(() -> registry.getMethodDescriptorForBaseline("unknown"))
                .hasMessageContaining("No definition found for glue line 'unknown'");
    }

    @Test
    public void testGetMethodForAssumeUnknownGlueLine() throws Exception {
        assertThatThrownBy(() -> registry.getMethodDescriptorForAssume("unknown"))
                .hasMessageContaining("No definition found for glue line 'unknown'");
    }

    @Test
    public void testGetMethodForSuccessUnknownGlueLine() throws Exception {
        assertThatThrownBy(() -> registry.getMethodDescriptorForSuccess("unknown"))
                .hasMessageContaining("No definition found for glue line 'unknown'");
    }

    @Test
    public void testGetMethodForFailureUnknownGlueLine() throws Exception {
        assertThatThrownBy(() -> registry.getMethodDescriptorForFailure("unknown"))
                .hasMessageContaining("No definition found for glue line 'unknown'");
    }

    @Test
    public void testGetDurationForTimeUnknownGlueLine() throws Exception {
        assertThatThrownBy(() -> registry.getDurationForTime("unknown"))
                .hasMessageContaining("No definition found for glue line unknown");
    }

    @Test
    public void testAddMethodForBaseline() throws Exception {
        final String glueLine = "baseline2";
        registry.addMethodDescriptorForBaseline(glueLine, baseLineDescriptor);
        assertEquals(2, registry.getBaselineDefinitions().size());
        assertTrue(registry.getBaselineDefinitions().containsKey(glueLine));
        assertEquals(baseLineDescriptor, registry.getBaselineDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForBaselineWithDuplicateName() throws Exception {
        final String glueLine = "baseline";
        assertThatThrownBy(() -> registry.addMethodDescriptorForBaseline(glueLine, baseLineDescriptor))
                .isInstanceOf(GarethAlreadyKnownDefinitionException.class)
                .hasMessageContaining("Glue line already registered for 'baseline'");
    }

    @Test
    public void testAddMethodForAssume() throws Exception {
        final String glueLine = "assume2";
        registry.addMethodDescriptorForAssume(glueLine, assumeDescriptor);
        assertEquals(2, registry.getAssumeDefinitions().size());
        assertTrue(registry.getAssumeDefinitions().containsKey(glueLine));
        assertEquals(assumeDescriptor, registry.getAssumeDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForAssumeWithDuplicateName() throws Exception {
        final String glueLine = "assume";
        assertThatThrownBy(() -> registry.addMethodDescriptorForAssume(glueLine, assumeDescriptor))
                .isInstanceOf(GarethAlreadyKnownDefinitionException.class)
                .hasMessageContaining("Glue line already registered for 'assume'");
    }

    @Test
    public void testAddMethodForSuccess() throws Exception {
        final String glueLine = "success2";
        registry.addMethodDescriptorForSuccess(glueLine, successDescriptor);
        assertEquals(2, registry.getSuccessDefinitions().size());
        assertTrue(registry.getSuccessDefinitions().containsKey(glueLine));
        assertEquals(successDescriptor, registry.getSuccessDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForSuccessWithDuplicateName() throws Exception {
        final String glueLine = "success";
        assertThatThrownBy(() -> registry.addMethodDescriptorForSuccess(glueLine, successDescriptor))
                .isInstanceOf(GarethAlreadyKnownDefinitionException.class)
                .hasMessageContaining("Glue line already registered for 'success'");
    }

    @Test
    public void testAddMethodForFailure() throws Exception {
        final String glueLine = "failure2";
        registry.addMethodDescriptorForFailure(glueLine, failureDescriptor);
        assertEquals(2, registry.getFailureDefinitions().size());
        assertTrue(registry.getFailureDefinitions().containsKey(glueLine));
        assertEquals(failureDescriptor, registry.getFailureDefinitions().get(glueLine));
    }

    @Test
    public void testAddMethodForFailureWithDuplicateName() throws Exception {
        final String glueLine = "failure";
        assertThatThrownBy(() -> registry.addMethodDescriptorForFailure(glueLine, failureDescriptor))
                .isInstanceOf(GarethAlreadyKnownDefinitionException.class)
                .hasMessageContaining("Glue line already registered for 'failure'");

    }

    @Test
    public void testAddDurationForTime() throws Exception {
        final String glueLine = "time2";
        registry.addDurationForTime(glueLine, duration);
        assertEquals(2, registry.getTimeDefinitions().size());
        assertTrue(registry.getTimeDefinitions().containsKey(glueLine));
        assertEquals(duration, registry.getTimeDefinitions().get(glueLine));
    }

    public void dummyMethod() {

    }
}