package org.craftsmenlabs.gareth.execution.registry;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.model.GlueLineType;
import org.craftsmenlabs.gareth.execution.invoker.RegexMethodDescriptor;
import org.craftsmenlabs.gareth.execution.parser.ParsedDefinition;

public class DefinitionRegistry
{

	private final Map<String, Pattern> regexes = new HashMap();
	private final DurationExpressionParser durationExpressionParser = new DurationExpressionParser();
	private final Map<String, RegexMethodDescriptor> baselineDefinitions = new HashMap<>();
	private final Map<String, RegexMethodDescriptor> assumeDefinitions = new HashMap<>();
	private final Map<String, RegexMethodDescriptor> successDefinitions = new HashMap<>();
	private final Map<String, RegexMethodDescriptor> failureDefinitions = new HashMap<>();
	private final Map<String, Duration> timeDefinitions = new HashMap<>();

	public Map<GlueLineType, Set<String>> getGlueLinesPerCategory()
	{
		Map<GlueLineType, Set<String>> allPatterns = new HashMap<>();
		allPatterns.put(GlueLineType.ASSUMPTION, assumeDefinitions.keySet());
		allPatterns.put(GlueLineType.BASELINE, baselineDefinitions.keySet());
		allPatterns.put(GlueLineType.SUCCESS, successDefinitions.keySet());
		allPatterns.put(GlueLineType.FAILURE, failureDefinitions.keySet());
		allPatterns.put(GlueLineType.TIME, timeDefinitions.keySet());
		return allPatterns;
	}

	public RegexMethodDescriptor getMethodDescriptorForBaseline(final String glueLine)
	{
		return getDefinition(baselineDefinitions, glueLine);
	}

	public RegexMethodDescriptor getMethodDescriptorForAssume(final String glueLine)
	{
		return getDefinition(assumeDefinitions, glueLine);
	}

	public RegexMethodDescriptor getMethodDescriptorForSuccess(final String glueLine)
	{
		return getDefinition(successDefinitions, glueLine);
	}

	public RegexMethodDescriptor getMethodDescriptorForFailure(final String glueLine)
	{
		return getDefinition(failureDefinitions, glueLine);
	}

	public void addParsedDefinition(final ParsedDefinition parsedDefinition)
	{
		parsedDefinition.getBaselineDefinitions().forEach((k, v) -> addDefinition(baselineDefinitions, k, v));
		parsedDefinition.getAssumeDefinitions().forEach((k, v) -> addDefinition(assumeDefinitions, k, v));
		parsedDefinition.getFailureDefinitions().forEach((k, v) -> addDefinition(failureDefinitions, k, v));
		parsedDefinition.getSuccessDefinitions().forEach((k, v) -> addDefinition(successDefinitions, k, v));
		parsedDefinition.getTimeDefinitions().forEach((k, v) -> addDefinition(timeDefinitions, k, v));
	}

	public Duration getDurationForTime(final String glueLine)
	{
		return getTimeDefinition(timeDefinitions, glueLine);
	}

	private RegexMethodDescriptor getDefinition(final Map<String, RegexMethodDescriptor> valueMap, final String experimentLine)
	{
		Optional<RegexMethodDescriptor> match = valueMap.values().stream()
			.filter(md -> matchesPattern(experimentLine, md.getRegexPatternForGlueLine())).findFirst();
		return match.orElseThrow(() -> new GarethUnknownDefinitionException(String
			.format("No definition found for glue line '%s'", experimentLine)));
	}

	private <T> T getTimeDefinition(final Map<String, T> valueMap, final String experimentLine)
	{
		final Optional<String> match = valueMap.keySet().stream()
			.filter(annotationPattern -> matchesPattern(experimentLine, annotationPattern)).findFirst();
		if (match.isPresent())
		{
			return valueMap.get(match.get());
		}
		else
		{
			//if no custom Time method is available, try to parse the experiment glueline to a common expression
			final Optional<Duration> duration = durationExpressionParser.parse(experimentLine);
			return (T)duration
				.orElseThrow(() -> new GarethUnknownDefinitionException("No definition found for glue line " + experimentLine));
		}
	}

	private <T> void addDefinition(final Map<String, T> valueMap, final String glueLine, final T definition)
	{
		if (!valueMap.containsKey(glueLine))
		{
			valueMap.put(glueLine, definition);
		}
		else
		{
			throw new GarethAlreadyKnownDefinitionException(String.format("Glue line already registered for '%s'", glueLine));
		}
	}

	private boolean matchesPattern(final String experimentLine, final String pattern)
	{
		if (experimentLine == null || pattern == null)
		{
			return false;
		}
		return getGlueLinePattern(pattern).matcher(experimentLine).matches();
	}

	private Pattern getGlueLinePattern(String pattern)
	{
		if (regexes.containsKey(pattern))
		{
			return regexes.get(pattern);
		}
		Pattern compiled = Pattern.compile(pattern);
		regexes.put(pattern, compiled);
		return compiled;
	}

}
