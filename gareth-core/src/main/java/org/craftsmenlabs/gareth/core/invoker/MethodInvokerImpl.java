package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class MethodInvokerImpl implements MethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(MethodInvokerImpl.class);


    private final ReflectionHelper reflectionHelper;

    public MethodInvokerImpl(final ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }

    public void invoke(MethodDescriptor methodDescriptor) throws GarethInvocationException {
        invoke(null, methodDescriptor);
    }

    public void invoke(MethodDescriptor methodDescriptor, Storage storage) {
        invoke(null, methodDescriptor, storage);
    }

    @Override
    public void invoke(final String glueLineInExperiment, final MethodDescriptor methodDescriptor) throws GarethInvocationException {
        invoke(glueLineInExperiment, methodDescriptor, null);
    }

    @Override
    public void invoke(final String glueLineInExperiment, final MethodDescriptor methodDescriptor, final Storage storage) throws GarethInvocationException {
        try {
            logger.trace("Invoking method %s", methodDescriptor.getMethod().getName());
            Class<?> declaringClass = methodDescriptor.getMethod().getDeclaringClass();
            final Object declaringClassInstance = reflectionHelper
                    .getInstanceForClass(declaringClass);
            methodDescriptor.invokeWith(glueLineInExperiment, declaringClassInstance, storage);
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new GarethInvocationException(e);
        }
    }
}
