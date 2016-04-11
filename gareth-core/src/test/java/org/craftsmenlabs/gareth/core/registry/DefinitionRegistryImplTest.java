package org.craftsmenlabs.gareth.core.registry;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;
import java.time.Duration;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class DefinitionRegistryImplTest
{

	Method mockMethod;
	MethodDescriptor baseLineDescriptor;
	MethodDescriptor assumeDescriptor;
	MethodDescriptor successDescriptor;
	MethodDescriptor failureDescriptor;
	Duration duration;
	private DefinitionRegistryImpl definitionRegistry;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		mockMethod = this.getClass().getMethod("dummyMethod");
		baseLineDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "baseline");
		assumeDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "assume");
		successDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "success");
		failureDescriptor = new MethodDescriptorImpl(mockMethod, 0, false, "failure");

		duration = Duration.ofHours(2);
		// Add default registries
		definitionRegistry = new DefinitionRegistryImpl();
		definitionRegistry.getBaselineDefinitions().put("baseline", baseLineDescriptor);
		definitionRegistry.getAssumeDefinitions().put("assume", assumeDescriptor);
		definitionRegistry.getSuccessDefinitions().put("success", successDescriptor);
		definitionRegistry.getFailureDefinitions().put("failure", failureDescriptor);
		definitionRegistry.getTimeDefinitions().put("time", duration);
	}

	@Test
	public void testGetMethodForBaseline() throws Exception
	{
		assertEquals(baseLineDescriptor, definitionRegistry.getMethodDescriptorForBaseline("baseline"));
	}

	@Test
	public void testGetMethodForAssume() throws Exception
	{
		assertEquals(assumeDescriptor, definitionRegistry.getMethodDescriptorForAssume("assume"));
	}

	@Test
	public void testGetMethodForSuccess() throws Exception
	{
		assertEquals(successDescriptor, definitionRegistry.getMethodDescriptorForSuccess("success"));
	}

	@Test
	public void testGetMethodForFailure() throws Exception
	{
		assertEquals(failureDescriptor, definitionRegistry.getMethodDescriptorForFailure("failure"));
	}

	@Test
	public void testGetDurationForTime() throws Exception
	{
		assertEquals(duration, definitionRegistry.getDurationForTime("time"));
	}

	@Test
	public void testGetMethodForBaselineUnknownGlueLine() throws Exception
	{
		assertThatThrownBy(() -> definitionRegistry.getMethodDescriptorForBaseline("unknown"))
			.hasMessageContaining("No definition found for glue line 'unknown'");
	}

	@Test
	public void testGetMethodForAssumeUnknownGlueLine() throws Exception
	{
		assertThatThrownBy(() -> definitionRegistry.getMethodDescriptorForAssume("unknown"))
			.hasMessageContaining("No definition found for glue line 'unknown'");
	}

	@Test
	public void testGetMethodForSuccessUnknownGlueLine() throws Exception
	{
		assertThatThrownBy(() -> definitionRegistry.getMethodDescriptorForSuccess("unknown"))
			.hasMessageContaining("No definition found for glue line 'unknown'");
	}

	@Test
	public void testGetMethodForFailureUnknownGlueLine() throws Exception
	{
		assertThatThrownBy(() -> definitionRegistry.getMethodDescriptorForFailure("unknown"))
			.hasMessageContaining("No definition found for glue line 'unknown'");
	}

	@Test
	public void testGetDurationForTimeUnknownGlueLine() throws Exception
	{
		assertThatThrownBy(() -> definitionRegistry.getDurationForTime("unknown"))
			.hasMessageContaining("No definition found for glue line unknown");
	}

	@Test
	public void testAddMethodForBaseline() throws Exception
	{
		final String glueLine = "baseline2";
		definitionRegistry.addMethodDescriptorForBaseline(glueLine, baseLineDescriptor);
		assertEquals(2, definitionRegistry.getBaselineDefinitions().size());
		assertTrue(definitionRegistry.getBaselineDefinitions().containsKey(glueLine));
		assertEquals(baseLineDescriptor, definitionRegistry.getBaselineDefinitions().get(glueLine));
	}

	@Test
	public void testAddMethodForBaselineWithDuplicateName() throws Exception
	{
		final String glueLine = "baseline";
		assertThatThrownBy(() -> definitionRegistry.addMethodDescriptorForBaseline(glueLine, baseLineDescriptor))
			.isInstanceOf(GarethAlreadyKnownDefinitionException.class)
			.hasMessageContaining("Glue line already registered for 'baseline'");
	}

	@Test
	public void testAddMethodForAssume() throws Exception
	{
		final String glueLine = "assume2";
		definitionRegistry.addMethodDescriptorForAssume(glueLine, assumeDescriptor);
		assertEquals(2, definitionRegistry.getAssumeDefinitions().size());
		assertTrue(definitionRegistry.getAssumeDefinitions().containsKey(glueLine));
		assertEquals(assumeDescriptor, definitionRegistry.getAssumeDefinitions().get(glueLine));
	}

	@Test
	public void testAddMethodForAssumeWithDuplicateName() throws Exception
	{
		final String glueLine = "assume";
		assertThatThrownBy(() -> definitionRegistry.addMethodDescriptorForAssume(glueLine, assumeDescriptor))
			.isInstanceOf(GarethAlreadyKnownDefinitionException.class)
			.hasMessageContaining("Glue line already registered for 'assume'");
	}

	@Test
	public void testAddMethodForSuccess() throws Exception
	{
		final String glueLine = "success2";
		definitionRegistry.addMethodDescriptorForSuccess(glueLine, successDescriptor);
		assertEquals(2, definitionRegistry.getSuccessDefinitions().size());
		assertTrue(definitionRegistry.getSuccessDefinitions().containsKey(glueLine));
		assertEquals(successDescriptor, definitionRegistry.getSuccessDefinitions().get(glueLine));
	}

	@Test
	public void testAddMethodForSuccessWithDuplicateName() throws Exception
	{
		final String glueLine = "success";
		assertThatThrownBy(() -> definitionRegistry.addMethodDescriptorForSuccess(glueLine, successDescriptor))
			.isInstanceOf(GarethAlreadyKnownDefinitionException.class)
			.hasMessageContaining("Glue line already registered for 'success'");
	}

	@Test
	public void testAddMethodForFailure() throws Exception
	{
		final String glueLine = "failure2";
		definitionRegistry.addMethodDescriptorForFailure(glueLine, failureDescriptor);
		assertEquals(2, definitionRegistry.getFailureDefinitions().size());
		assertTrue(definitionRegistry.getFailureDefinitions().containsKey(glueLine));
		assertEquals(failureDescriptor, definitionRegistry.getFailureDefinitions().get(glueLine));
	}

	@Test
	public void testAddMethodForFailureWithDuplicateName() throws Exception
	{
		final String glueLine = "failure";
		assertThatThrownBy(() -> definitionRegistry.addMethodDescriptorForFailure(glueLine, failureDescriptor))
			.isInstanceOf(GarethAlreadyKnownDefinitionException.class)
			.hasMessageContaining("Glue line already registered for 'failure'");

	}

	@Test
	public void testAddDurationForTime() throws Exception
	{
		final String glueLine = "time2";
		definitionRegistry.addDurationForTime(glueLine, duration);
		assertEquals(2, definitionRegistry.getTimeDefinitions().size());
		assertTrue(definitionRegistry.getTimeDefinitions().containsKey(glueLine));
		assertEquals(duration, definitionRegistry.getTimeDefinitions().get(glueLine));
	}

	public void dummyMethod()
	{

	}
}