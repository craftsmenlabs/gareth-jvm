package org.craftsmenlabs.gareth.api.invoker;

import java.lang.reflect.Method;

/**
 * Created by hylke on 21/08/15.
 */
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

}
