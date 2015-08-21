package org.craftsmenlabs.gareth.api.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;

import java.lang.reflect.Method;

/**
 * Created by hylke on 11/08/15.
 */
public interface MethodInvoker {

    void invoke(final MethodDescriptor methodDescriptor) throws GarethInvocationException;

}
