package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.core.storage.DefaultStorage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodDescriptorImpl implements MethodDescriptor {

    private final Method method;
    private final int storageParameterIndex;
    private final boolean storageParameter;
    private final String pattern;

    public MethodDescriptorImpl(
            final Method method, final int storageParameterIndex, final boolean storageParameter, final String pattern) {
        this.method = method;
        this.storageParameterIndex = storageParameterIndex;
        this.storageParameter = storageParameter;
        this.pattern = pattern;
    }

    public MethodDescriptorImpl(final Method method, final int storageParameterIndex, final boolean storageParameter) {
        this(method, storageParameterIndex, storageParameter, null);
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public boolean hasStorage() {
        return this.storageParameter;
    }

    @Override
    public int getStorageIndex() {
        return this.storageParameterIndex;
    }

    public void invokeWith(String glueLineInExperiment, Object declaringClassInstance, DefaultStorage storage)
            throws InvocationTargetException, IllegalAccessException {
        if (hasStorage()) {
            getMethod().invoke(declaringClassInstance, storage);
        } else {
            getMethod().invoke(declaringClassInstance);
        }
    }

    public String getRegexPatternForGlueLine() {
        return pattern;
    }
}
