package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MethodInvokerImplTest {

    private static final List<String> invokeMessageList = new ArrayList<>();
    private MethodInvokerImpl methodInvoker;
    private MethodDescriptor methodDescriptor;

    @Before
    public void setUp() throws Exception {
        invokeMessageList.clear();
        methodInvoker = new MethodInvokerImpl(new ReflectionHelper(null));
        methodDescriptor = new MethodDescriptorImpl(getStubMethod(), 0, false);
    }

    @Test
    public void testInvoke() throws Exception {
        methodInvoker.invoke(methodDescriptor);
        assertEquals(1, invokeMessageList.size());
        assertEquals("stub", invokeMessageList.get(0));
    }

    @Test
    public void testInvokeWithNullStorage() throws Exception {
        methodInvoker.invoke(methodDescriptor, null);
        assertEquals(1, invokeMessageList.size());
        assertEquals("stub", invokeMessageList.get(0));
    }

    @Test
    public void testInvokeWithStorageParameterWithNullStorage() throws Exception {
        final MethodDescriptor methodDescriptor = new MethodDescriptorImpl(getStubMethodWithStorageParameter(), 0, true);
        methodInvoker.invoke(methodDescriptor, null);
        assertEquals(1, invokeMessageList.size());
        assertEquals("stub with storage", invokeMessageList.get(0));
    }

    @Test
    public void testInvokeWithStorageParameterWithStorage() throws Exception {
        final MethodDescriptor methodDescriptor = new MethodDescriptorImpl(getStubMethodWithStorageParameter(), 0, true);
        final DefaultStorage storage = new DefaultStorage();
        methodInvoker.invoke(methodDescriptor, storage);
        assertEquals(1, invokeMessageList.size());
        assertEquals("stub with storage", invokeMessageList.get(0));
        assertTrue(storage.get("store").isPresent());
        assertEquals("value", storage.get("store").get());
    }

    private Method getStubMethod() throws Exception {
        return this.getClass().getMethod("stubMethod");
    }

    private Method getStubMethodWithStorageParameter() throws Exception {
        return this.getClass().getMethod("stubMethod", DefaultStorage.class);
    }

    public void stubMethod() throws Exception {
        invokeMessageList.add("stub");
    }

    public void stubMethod(final DefaultStorage storage) {
        if (null != storage) storage.store("store", "value");
        invokeMessageList.add("stub with storage");
    }
}