package org.craftsmenlabs.gareth.core.invoker;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hylke on 18/09/15.
 */
public class MethodDescriptorImplTest {


    private MethodDescriptorImpl methodDescriptor;

    private Method stubMethod;

    @Before
    public void before() {
        stubMethod = stubMethod();
        methodDescriptor = new MethodDescriptorImpl(stubMethod, 1, true);
    }


    @Test
    public void testGetMethod() throws Exception {
        assertEquals(stubMethod, methodDescriptor.getMethod());
    }

    @Test
    public void testHasStorage() throws Exception {
        assertTrue(methodDescriptor.hasStorage());
    }

    @Test
    public void testGetStorageIndex() throws Exception {
        assertEquals(1, methodDescriptor.getStorageIndex());
    }

    private Method stubMethod() {
        return this.getClass().getMethods()[0];
    }
}