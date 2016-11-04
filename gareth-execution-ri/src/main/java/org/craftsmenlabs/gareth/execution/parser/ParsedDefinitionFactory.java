package org.craftsmenlabs.gareth.execution.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.craftsmenlabs.gareth.api.annotation.*;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.execution.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsedDefinitionFactory {

    private static Logger LOGGER = LoggerFactory.getLogger(ParsedDefinitionFactory.class);
    private final DefinitionFactory _definitionFactory;

    public ParsedDefinitionFactory(final DefinitionFactory definitionFactory) {
        this._definitionFactory = definitionFactory;
    }

    public ParsedDefinition parse(final Class clazz) throws GarethExperimentParseException {
        if (clazz == null) throw new IllegalArgumentException("Class cannot be null");
        final ParsedDefinition parsedDefinition = new ParsedDefinition();
        Stream.of(clazz.getMethods())
              .forEach(m -> parseMethod(m, parsedDefinition));
        return parsedDefinition;
    }

    /**
     * Parse a single method
     *
     * @param method
     * @param definition
     */
    private void parseMethod(final Method method, final ParsedDefinition definition) {
        Optional<Baseline> baseline = getAnnotation(method, Baseline.class);
        if (baseline.isPresent()) {
            definition.getBaselineDefinitions()
                      .put(baseline.get().glueLine(), createMethod(method, baseline.get().glueLine()));
        }
        Optional<Assume> assume = getAnnotation(method, Assume.class);
        if (assume.isPresent()) {
            definition.getAssumeDefinitions()
                      .put(assume.get().glueLine(), createMethod(method, assume.get().glueLine()));
        }
        Optional<Success> sucess = getAnnotation(method, Success.class);
        if (sucess.isPresent()) {
            definition.getSuccessDefinitions()
                      .put(sucess.get().glueLine(), createMethod(method, sucess.get().glueLine()));
        }
        Optional<Failure> failure = getAnnotation(method, Failure.class);
        if (failure.isPresent()) {
            definition.getFailureDefinitions()
                      .put(failure.get().glueLine(), createMethod(method, failure.get().glueLine()));
        }
        Optional<Time> time = getAnnotation(method, Time.class);
        if (time.isPresent()) {
            registerDuration(method, time.get().glueLine(), definition.getTimeDefinitions());
        }
    }

    public <T> Optional<T> getAnnotation(Method method, Class<T> annotationClass) {
        Optional<Annotation> first = Stream.of(method.getDeclaredAnnotations())
                                           .filter(an -> an.annotationType().getName()
                                                           .equals(annotationClass.getName())).findFirst();
        return (Optional<T>) first;
    }

    private RegexMethodDescriptor createMethod(final Method method, final String glueLine) {
        if (isValidMethod(method)) {
            LOGGER.info("Found valid method {} for glueline {}", method.getName(), glueLine);
            return new RegexMethodDescriptor(glueLine, method, 0, hasStorageParameter(method));

        } else {
            throw new IllegalStateException(String
                    .format("Method %s with glue line '%s' is not a valid method (no void return type)", method
                            .getName(), glueLine));
        }
    }

    /**
     * Register duration based on method outcome
     *
     * @param method
     * @param glueLine
     * @param durationMap
     */
    private void registerDuration(final Method method, final String glueLine, final Map<String, Duration> durationMap) throws GarethDefinitionParseException {
        if (isTimeMethod(method)) {
            try {
                final Object tmpDefinition = _definitionFactory.getInstanceForClass(method.getDeclaringClass());
                durationMap.put(glueLine, (Duration) method.invoke(tmpDefinition));
            } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new GarethDefinitionParseException(e);
            }
        } else {
            throw new IllegalStateException(String
                    .format("Method %s with glue line '%s' is not a valid method (no duration return type)", method
                            .getName(), glueLine));
        }
    }

    private boolean isValidMethod(final Method method) {
        return (method.getReturnType().equals(Void.class)
                || method.getReturnType().equals(Void.TYPE));
    }

    private boolean hasStorageParameter(Method method) {
        return method.getParameterCount() > 0 && method.getParameters()[0]
                .getParameterizedType() == DefaultStorage.class;
    }

    private boolean isTimeMethod(final Method method) {
        return method.getReturnType().isAssignableFrom(Duration.class);
    }

    private boolean isRegexTimeMethod(final Method method) {
        return isTimeMethod(method) &&
                method.getParameterCount() == 2 &&
                method.getParameterTypes()
                      .equals(new Object[]{Integer.class, String.class});
    }
}
