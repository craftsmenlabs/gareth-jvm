package org.craftsmenlabs.gareth.core.registry;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by hylke on 10/08/15.
 */
@Getter
public class DefinitionRegistryImpl implements DefinitionRegistry {

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
        return getDefinition(getTimeDefinitions(), glueLine);
    }

    @Override
    public void addDurationForTime(final String glueLine, final  Duration duration) {
        addDefinition(getTimeDefinitions(), glueLine, duration);
    }

    private <T> T getDefinition(final Map<String, T> valueMap, final String glueLine) {
        return Optional.ofNullable(valueMap.get(glueLine)).orElseThrow(() -> new GarethUnknownDefinitionException(String.format("No definition found for glue line '%s'", glueLine)));
    }

    private <T> void addDefinition(final Map<String, T> valueMap, final String glueLine, final T definition) {
        if (!valueMap.containsKey(glueLine)) {
            valueMap.put(glueLine, definition);
        } else {
            throw new GarethAlreadyKnownDefinitionException(String.format("Glue line already registered for '%s'", glueLine));
        }
    }
}
