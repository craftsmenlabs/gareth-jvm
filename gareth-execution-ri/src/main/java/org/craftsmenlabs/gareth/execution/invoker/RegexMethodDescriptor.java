package org.craftsmenlabs.gareth.execution.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RegexMethodDescriptor {

    private final RegexAnnotatedMethodAdapter methodAdapter;
    private final int storageParameterIndex;
    private final boolean storageParameter;

    public RegexMethodDescriptor(final String glueLine,
                                 final Method method,
                                 final int storageParameterIndex,
                                 final boolean storageParameter) {
        this.methodAdapter = new RegexAnnotatedMethodAdapter(method, glueLine);
        this.storageParameterIndex = storageParameterIndex;
        this.storageParameter = storageParameter;
    }

    public Method getMethod() {
        return this.methodAdapter.getMethod();
    }

    public boolean hasStorage() {
        return this.storageParameter;
    }

    public int getStorageIndex() {
        return this.storageParameterIndex;
    }

    public void invokeWith(String glueLineInExperiment, Object declaringClassInstance, DefaultStorage storage) throws InvocationTargetException, IllegalAccessException {
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
