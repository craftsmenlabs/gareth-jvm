package org.craftsmenlabs.gareth.api.annotation;

/**
 * Created by hylke on 10/08/15.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hylke on 10/08/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Failure {
    public String glueLine();
}
