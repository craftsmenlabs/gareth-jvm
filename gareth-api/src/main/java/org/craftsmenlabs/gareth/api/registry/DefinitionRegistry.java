package org.craftsmenlabs.gareth.api.registry;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.GlueLineType;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public interface DefinitionRegistry
{

	Map<GlueLineType, Set<String>> getGlueLinesPerCategory();

	/**
	 * Get method for baseline
	 *
	 * @param glueLine
	 * @return
	 */
	MethodDescriptor getMethodDescriptorForBaseline(final String glueLine);

	/**
	 * Get method assume
	 *
	 * @param glueLine
	 * @return
	 */
	MethodDescriptor getMethodDescriptorForAssume(final String glueLine);

	/**
	 * Get method for success
	 *
	 * @param glueLine
	 * @return
	 */
	MethodDescriptor getMethodDescriptorForSuccess(final String glueLine);

	/**
	 * Get method for failure
	 *
	 * @param glueLine
	 * @return
	 */
	MethodDescriptor getMethodDescriptorForFailure(final String glueLine);

	/**
	 * Get duration for time
	 *
	 * @param glueLine
	 * @return
	 */
	Duration getDurationForTime(final String glueLine);

	/**
	 * Add method for baseline
	 *
	 * @param glueLine
	 * @param methodDescriptor
	 */
	void addMethodDescriptorForBaseline(final String glueLine, final MethodDescriptor methodDescriptor);

	/**
	 * Add method for assume
	 *
	 * @param glueLine
	 * @param methodDescriptor
	 */
	void addMethodDescriptorForAssume(final String glueLine, final MethodDescriptor methodDescriptor);

	/**
	 * Add method for success
	 *
	 * @param glueLine
	 * @param methodDescriptor
	 */
	void addMethodDescriptorForSuccess(final String glueLine, final MethodDescriptor methodDescriptor);

	/**
	 * Add method for failure
	 *
	 * @param glueLine
	 * @param methodDescriptor
	 */
	void addMethodDescriptorForFailure(final String glueLine, final MethodDescriptor methodDescriptor);

	/**
	 * Add duration for time
	 *
	 * @param glueLine
	 * @param duration
	 */
	void addDurationForTime(final String glueLine, final Duration duration);

}
