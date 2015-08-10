package org.craftsmenlabs.gareth.core.parser;

import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.definition.DefinitionType;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by hylke on 10/08/15.
 */
public class ParsedParsedDefinitionFactoryImpl implements ParsedDefinitionFactory {

    private final static Set<Class> allowedAnnotations = new HashSet<>(Arrays.asList(Baseline.class, Assume.class, Time.class, Success.class, Failure.class));


    @Override
    public ParsedDefinition parse(final Class clazz) throws GarethExperimentParseException {
        Optional
                .ofNullable(clazz)
                .orElseThrow(() -> new IllegalArgumentException("Class cannot be null"));

        final ParsedDefinition parsedDefinition = new ParsedDefinitionImpl();
        parseClass(clazz, parsedDefinition);
        return parsedDefinition;
    }


    /**
     * Parse class
     *
     * @param clazz
     * @param parsedDefinition
     */
    private void parseClass(final Class clazz, final ParsedDefinition parsedDefinition) {
        Stream
                .of(clazz.getMethods())
                .parallel()
                .forEach(m -> parseMethod(m, parsedDefinition));
    }

    /**
     * Parse a single method
     *
     * @param method
     * @param parsedDefinition
     */
    private void parseMethod(final Method method, final ParsedDefinition parsedDefinition) {
        Optional.ofNullable(method.getAnnotation(Baseline.class)).ifPresent(baseline -> {
            registerUnitOfWork(method, baseline.glueLine(), parsedDefinition.getBaselineDefinitions());
        });
        Optional.ofNullable(method.getAnnotation(Assume.class)).ifPresent(assume -> {
            registerUnitOfWork(method, assume.glueLine(), parsedDefinition.getAssumeDefinitions());
        });
        Optional.ofNullable(method.getAnnotation(Success.class)).ifPresent(success -> {
            registerUnitOfWork(method, success.glueLine(), parsedDefinition.getSuccessDefinitions());
        });
        Optional.ofNullable(method.getAnnotation(Failure.class)).ifPresent(failure -> {
            registerUnitOfWork(method, failure.glueLine(), parsedDefinition.getFailureDefinitions());
        });
    }

    private void registerUnitOfWork(final Method method, final String glueLine, final Map<String, Method> unitOfWorkMap) {
        if (isValidMethod(method)) {
            unitOfWorkMap.put(glueLine, method);
        } else {
            throw new IllegalStateException(String.format("Method %s with glue line '%s' is not a valid method (no void return type)", method.getName(), glueLine));
        }
    }

    private boolean isValidMethod(final Method method) {
        return method.getReturnType().equals(Void.class)
                || method.getReturnType().equals(Void.TYPE);
    }

    private boolean isValidateTimeMethod(final Method method) {
        return method.getReturnType().isAssignableFrom(Duration.class);
    }
}
