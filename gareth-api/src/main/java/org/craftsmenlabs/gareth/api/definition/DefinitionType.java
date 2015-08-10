package org.craftsmenlabs.gareth.api.definition;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.annotation.*;

/**
 * Created by hylke on 10/08/15.
 */
public enum DefinitionType {
    BASELINE(Baseline.class),
    ASSUME(Assume.class),
    TIME(Time.class),
    SUCCESS(Success.class),
    FAILURE(Failure.class);

    @Getter
    private final Class annotationClass;

    DefinitionType(final Class annotationClass) {
        this.annotationClass = annotationClass;
    }


    public static final DefinitionType getDefinitionType(final Class annotationClass) {
        for (final DefinitionType definitionType : DefinitionType.values()) {
            if (definitionType.getAnnotationClass().equals(annotationClass)) {
                return definitionType;
            }
        }
        throw new IllegalStateException(String.format("DefinitionType not found for class %s", annotationClass));
    }


}
