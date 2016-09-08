package org.craftsmenlabs.gareth.application.definition;

import org.craftsmenlabs.gareth.core.reflection.DefinitionFactory;

public class RestDefinitionFactory implements DefinitionFactory {

    @Override
    public Object createDefinition(Class clazz) {
        System.out.println("CreateDefinition for class " + clazz);

        if (clazz.equals(AnotherDefinition.class)) {
            return new AnotherDefinition(new Object());
        }

        return null;
    }
}
