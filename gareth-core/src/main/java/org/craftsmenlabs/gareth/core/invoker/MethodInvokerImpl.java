package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hylke on 13/08/15.
 */
public class MethodInvokerImpl implements MethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(MethodInvokerImpl.class);


    private final ReflectionHelper reflectionHelper;

    public MethodInvokerImpl(final ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }

    @Override
    public void invoke(final MethodDescriptor methodDescriptor) throws GarethInvocationException {
        try {
            logger.trace("Invoking method %s", methodDescriptor.getMethod().getName());
            final Object declaringClassInstance = reflectionHelper.getInstanceForClass(methodDescriptor.getMethod().getDeclaringClass());
            methodDescriptor.getMethod().invoke(declaringClassInstance);
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new GarethInvocationException(e);
        }
    }
}
