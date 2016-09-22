package org.craftsmenlabs.gareth.core.invoker;

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

    void invokeWith(String glueLineInExperiment, Object declaringClassInstance, org.craftsmenlabs.gareth.core.storage.DefaultStorage storage) throws InvocationTargetException, IllegalAccessException;
}
