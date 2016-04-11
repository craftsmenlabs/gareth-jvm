package org.craftsmenlabs.gareth.api.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.storage.Storage;

public interface MethodInvoker {

    void invoke(final MethodDescriptor methodDescriptor) throws GarethInvocationException;

    void invoke(final MethodDescriptor methodDescriptor, final Storage storage) throws GarethInvocationException;

    /**
     * Method invoke without storage
     *
     * @param methodDescriptor
     * @throws GarethInvocationException
     */
    void invoke(final String glueLineInExperiment, final MethodDescriptor methodDescriptor) throws GarethInvocationException;

    /**
     * Method invoke with storage
     *
     * @param methodDescriptor
     * @param storage
     * @throws GarethInvocationException
     */
    void invoke(final String glueLineInExperiment, final MethodDescriptor methodDescriptor, final Storage storage) throws GarethInvocationException;

}
