package org.craftsmenlabs.gareth.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by hylke on 13/08/15.
 */
public class ReflectionHelper {

    private DefinitionFactory customDefinitionFactory;

    public ReflectionHelper(DefinitionFactory customDefinitionFactory) {
        this.customDefinitionFactory = customDefinitionFactory;
    }

    /**
     * Create a instance for particular class (only zero argument constructors supported)
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public Object getInstanceForClass(final Class clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        if (customDefinitionFactory != null) {
            Object definition = customDefinitionFactory.createDefinition(clazz);
            if (definition != null) {
                return definition;
            }
        }

        Constructor constructor = null;
        Object declaringClassInstance = null;

        final boolean memberClass = clazz.isMemberClass();
        final int requiredConstructorArguments = memberClass ? 1 : 0; //

        if (memberClass) {
            declaringClassInstance = getInstanceForClass(clazz.getDeclaringClass());
        }
        for (final Constructor declaredConstructor : clazz.getDeclaredConstructors()) {
            if (declaredConstructor.getGenericParameterTypes().length == requiredConstructorArguments) {
                constructor = declaredConstructor;
                break;
            }
        }
        // If a valid constructor is available
        if (null != constructor) {
            final Object instance;
            constructor.setAccessible(true);
            if (memberClass) {
                instance = constructor.newInstance(declaringClassInstance);
            } else {
                instance = constructor.newInstance();
            }
            return instance;
        }
        throw new InstantiationException(String.format("Class %s has no zero argument argument constructor", clazz));
    }
}
