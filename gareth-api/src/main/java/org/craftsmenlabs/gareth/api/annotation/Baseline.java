package org.craftsmenlabs.gareth.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Baseline {

    String glueLine();

}