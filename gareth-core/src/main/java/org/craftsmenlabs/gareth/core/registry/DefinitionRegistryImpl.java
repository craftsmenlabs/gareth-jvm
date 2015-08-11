package org.craftsmenlabs.gareth.core.registry;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
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

    private Map<String, Method> baselineDefinitions = new HashMap<>();
    private Map<String, Method> assumeDefinitions = new HashMap<>();
    private Map<String, Method> successDefinitions = new HashMap<>();
    private Map<String, Method> failureDefinitions = new HashMap<>();
    private Map<String, Duration> timeDefinitions = new HashMap<>();

    @Override
    public Method getMethodForBaseline(final String glueLine) {
        return getDefinition(getBaselineDefinitions(), glueLine);
    }

    @Override
    public Method getMethodForAssume(final String glueLine) {
        return getDefinition(getAssumeDefinitions(), glueLine);
    }

    @Override
    public Method getMethodForSuccess(final String glueLine) {
        return getDefinition(getSuccessDefinitions(), glueLine);
    }

    @Override
    public Method getMethodForFailure(final String glueLine) {
        return getDefinition(getFailureDefinitions(), glueLine);
    }

    @Override
    public void addMethodForBaseline(final String glueLine, final Method method) {
        addDefinition(getBaselineDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodForAssume(final String glueLine, final Method method) {
        addDefinition(getAssumeDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodForSuccess(final String glueLine, final Method method) {
        addDefinition(getSuccessDefinitions(), glueLine, method);
    }

    @Override
    public void addMethodForFailure(final String glueLine, final Method method) {
        addDefinition(getFailureDefinitions(), glueLine, method);
    }


    @Override
    public Duration getDurationForTime(String glueLine) {
        return getDefinition(getTimeDefinitions(), glueLine);
    }

    @Override
    public void addDurationForTime(String glueLine, Duration duration) {
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
