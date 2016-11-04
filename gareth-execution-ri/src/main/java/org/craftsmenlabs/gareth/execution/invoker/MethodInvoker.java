package org.craftsmenlabs.gareth.execution.invoker;

import java.lang.reflect.InvocationTargetException;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(MethodInvoker.class);

    private final DefinitionFactory _definitionFactory;

    public MethodInvoker(final DefinitionFactory definitionFactory) {
        this._definitionFactory = definitionFactory;
    }

    public void invoke(RegexMethodDescriptor methodDescriptor) throws GarethInvocationException {
        invoke(null, methodDescriptor);
    }

    public void invoke(RegexMethodDescriptor methodDescriptor, DefaultStorage storage) {
        invoke(null, methodDescriptor, storage);
    }

    public void invoke(final String glueLineInExperiment, final RegexMethodDescriptor methodDescriptor) throws GarethInvocationException {
        invoke(glueLineInExperiment, methodDescriptor, null);
    }

    public void invoke(final String glueLineInExperiment, final RegexMethodDescriptor methodDescriptor, final DefaultStorage storage) throws GarethInvocationException {
        try {
            logger.trace("Invoking method %s", methodDescriptor.getMethod().getName());
            Class<?> declaringClass = methodDescriptor.getMethod().getDeclaringClass();
            final Object declaringClassInstance = _definitionFactory
                    .getInstanceForClass(declaringClass);
            methodDescriptor.invokeWith(glueLineInExperiment, declaringClassInstance, storage);
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new GarethInvocationException(e);
        }
    }
}
