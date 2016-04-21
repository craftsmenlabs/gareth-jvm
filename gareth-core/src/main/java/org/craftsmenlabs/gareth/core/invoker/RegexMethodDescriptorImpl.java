package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RegexMethodDescriptorImpl implements MethodDescriptor {

    private final RegexAnnotatedMethodAdapter methodAdapter;
    private final int storageParameterIndex;
    private final boolean storageParameter;

    public RegexMethodDescriptorImpl(final String glueLine,
                                     final Method method,
                                     final int storageParameterIndex,
                                     final boolean storageParameter) {
        this.methodAdapter = new RegexAnnotatedMethodAdapter(method, glueLine);
        this.storageParameterIndex = storageParameterIndex;
        this.storageParameter = storageParameter;
    }

    @Override
    public Method getMethod() {
        return this.methodAdapter.getMethod();
    }

    @Override
    public boolean hasStorage() {
        return this.storageParameter;
    }

    @Override
    public int getStorageIndex() {
        return this.storageParameterIndex;
    }

    public void invokeWith(String glueLineInExperiment, Object declaringClassInstance, Storage storage) throws InvocationTargetException, IllegalAccessException {
        List<Object> arguments = new ArrayList<>();
        if (hasStorage())
            arguments.add(storage);
        List<Object> argumentValuesFromInputString = methodAdapter
                .getArgumentValuesFromInputString(glueLineInExperiment);
        arguments.addAll(argumentValuesFromInputString);
        methodAdapter.invokeWith(declaringClassInstance, arguments);
    }

    public String getRegexPatternForGlueLine() {
        return methodAdapter.getPattern();
    }
}
