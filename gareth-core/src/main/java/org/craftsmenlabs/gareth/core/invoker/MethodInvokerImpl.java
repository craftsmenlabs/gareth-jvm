package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hylke on 13/08/15.
 */
public class MethodInvokerImpl implements MethodInvoker {

    private final ReflectionHelper reflectionHelper;

    public MethodInvokerImpl(final ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }

    @Override
    public void invoke(final Method method) throws GarethInvocationException {
        try {
            final Object declaringClassInstance = reflectionHelper.getInstanceForClass(method.getDeclaringClass());
            method.invoke(declaringClassInstance);
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new GarethInvocationException(e);
        }
    }
}
