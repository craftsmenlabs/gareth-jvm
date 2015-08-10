package org.craftsmenlabs.gareth.core.registry;

import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Created by hylke on 10/08/15.
 */
public class DefinitionRegistryImpl implements DefinitionRegistry {

    @Override
    public Method getMethodForBaseline(final String glueLine) {
        return null;
    }

    @Override
    public Method getMethodForAssume(final String glueLine) {
        return null;
    }

    @Override
    public Method getMethodForSuccess(final String glueLine) {
        return null;
    }

    @Override
    public Method getMethodForFailure(final String glueLine) {
        return null;
    }

    @Override
    public void addMethodForBaseline(final String glueLine, final Method method) {

    }

    @Override
    public void addMethodForAssume(final String glueLine, final Method method) {

    }

    @Override
    public void addMethodForSuccess(final String glueLine, final Method method) {

    }

    @Override
    public void addMethodForFailure(final String glueLine, final Method method) {

    }


    @Override
    public Duration getDurationForTime(String glueLine) {
        return null;
    }

    @Override
    public void addDurationForTime(String glueLine, Duration duration) {

    }
}
