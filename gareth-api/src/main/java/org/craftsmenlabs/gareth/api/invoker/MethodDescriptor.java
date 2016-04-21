package org.craftsmenlabs.gareth.api.invoker;

import org.craftsmenlabs.gareth.api.storage.Storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface MethodDescriptor {

    /**
     * Get the method
     *
     * @return
     */
    Method getMethod();

    /**
     * Method has storage capabilities
     *
     * @return
     */
    boolean hasStorage();


    /**
     * Get the index of the storage parameter
     *
     * @return
     */
    int getStorageIndex();

    String getRegexPatternForGlueLine();

    void invokeWith(String glueLineInExperiment, Object declaringClassInstance, Storage storage) throws InvocationTargetException, IllegalAccessException;
}
