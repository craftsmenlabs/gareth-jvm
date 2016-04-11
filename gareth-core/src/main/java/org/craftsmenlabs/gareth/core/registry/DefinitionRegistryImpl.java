package org.craftsmenlabs.gareth.core.registry;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.parser.CommonDurationExpressionParser;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by hylke on 10/08/15.
 */
@Getter
public class DefinitionRegistryImpl implements DefinitionRegistry {

    private final Map<String, Pattern> regexes = new HashMap();
    CommonDurationExpressionParser durationExpressionParser = new CommonDurationExpressionParser();
    private final Map<String, MethodDescriptor> baselineDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> assumeDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> successDefinitions = new HashMap<>();
    private final Map<String, MethodDescriptor> failureDefinitions = new HashMap<>();
    private final Map<String, Duration> timeDefinitions = new HashMap<>();

    @Override
    public MethodDescriptor getMethodDescriptorForBaseline(final String glueLine) {
        return getDefinition(getBaselineDefinitions(), glueLine);
    }

    @Override
    public MethodDescriptor getMethodDescriptorForAssume(final String glueLine) {
        return getDefinition(getAssumeDefinitions(), glueLine);
    }

    @Override
    public MethodDescriptor getMethodDescriptorForSuccess(final String glueLine) {
        return getDefinition(getSuccessDefinitions(), glueLine);
    }

    @Override
    public MethodDescriptor getMethodDescriptorForFailure(final String glueLine) {
        return getDefinition(getFailureDefinitions(), glueLine);
    }

    @Override
    public void addMethodDescriptorForBaseline(final String glueLine, final MethodDescriptor method) {
        addDefinition(getBaselineDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodDescriptorForAssume(final String glueLine, final MethodDescriptor method) {
        addDefinition(getAssumeDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodDescriptorForSuccess(final String glueLine, final MethodDescriptor method) {
        addDefinition(getSuccessDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodDescriptorForFailure(final String glueLine, final MethodDescriptor method) {
        addDefinition(getFailureDefinitions(), glueLine, method);
    }

    @Override
    public Duration getDurationForTime(final String glueLine) {
        return getTimeDefinition(getTimeDefinitions(), glueLine);
    }

    @Override
    public void addDurationForTime(final String glueLine, final Duration duration) {
        addDefinition(getTimeDefinitions(), glueLine, duration);
    }

    private MethodDescriptor getDefinition(final Map<String, MethodDescriptor> valueMap, final String experimentLine) {
        Optional<MethodDescriptor> match = valueMap.values().stream()
                                                   .filter(md -> matchesPattern(experimentLine, md.getPattern()))
                                                   .findFirst();
        return match.orElseThrow(() -> new GarethUnknownDefinitionException(String
                .format("No definition found for glue line '%s'", experimentLine)));
    }

    private <T> T getTimeDefinition(final Map<String, T> valueMap, final String glueLine) {
        Optional<String> match = valueMap.keySet().stream().filter(pattern -> matchesPattern(glueLine, pattern))
                                         .findFirst();
        if (match.isPresent())
            return valueMap.get(match.get());
        else {
            //if no custom Time method is available, try to parse the experiment glueline to a common expression
            Optional<Duration> duration = durationExpressionParser.parse(glueLine);
            return (T) duration
                    .orElseThrow(() -> new GarethUnknownDefinitionException("No definition found for glue line " + glueLine));
        }
    }

    private <T> void addDefinition(final Map<String, T> valueMap, final String glueLine, final T definition) {
        if (!valueMap.containsKey(glueLine)) {
            valueMap.put(glueLine, definition);
        } else {
            throw new GarethAlreadyKnownDefinitionException(String
                    .format("Glue line already registered for '%s'", glueLine));
        }
    }

    private boolean matchesPattern(String experimentLine, String pattern) {
        if (experimentLine == null || pattern == null)
            return false;
        return getGlueLinePattern(pattern).matcher(experimentLine).matches();
    }

    private Pattern getGlueLinePattern(String pattern) {
        if (regexes.containsKey(pattern))
            return regexes.get(pattern);
        Pattern compiled = Pattern.compile(pattern);
        regexes.put(pattern, compiled);
        return compiled;
    }

}
