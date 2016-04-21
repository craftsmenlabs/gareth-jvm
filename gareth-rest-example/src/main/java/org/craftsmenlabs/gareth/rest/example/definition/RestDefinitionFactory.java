package org.craftsmenlabs.gareth.rest.example.definition;

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
