package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;

import java.lang.reflect.Method;

/**
 * Created by hylke on 21/08/15.
 */
public class MethodDescriptorImpl implements MethodDescriptor {

    private final Method method;
    private final int storageParameterIndex;
    private final boolean storageParameter;

    public MethodDescriptorImpl(final Method method, final int storageParameterIndex, final boolean storageParameter) {
        this.method = method;
        this.storageParameterIndex = storageParameterIndex;
        this.storageParameter = storageParameter;
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
}
