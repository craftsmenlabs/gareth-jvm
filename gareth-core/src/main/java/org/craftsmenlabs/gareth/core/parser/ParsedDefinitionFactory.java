package org.craftsmenlabs.gareth.core.parser;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.core.invoker.RegexMethodDescriptorImpl;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ParsedDefinitionFactory {

    private final ReflectionHelper reflectionHelper;

    public ParsedDefinitionFactory(final ReflectionHelper reflectionHelper) {
        this.reflectionHelper = reflectionHelper;
    }

    public ParsedDefinition parse(final Class clazz) throws GarethExperimentParseException {
        Optional.ofNullable(clazz)
                .orElseThrow(() -> new IllegalArgumentException("Class cannot be null"));

        final ParsedDefinition parsedDefinition = new ParsedDefinition();
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
        Stream.of(clazz.getMethods())
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
        Optional.ofNullable(method.getAnnotation(Time.class)).ifPresent(time -> {
            registerDuration(method, time.glueLine(), parsedDefinition.getTimeDefinitions());
        });
    }

    private void registerUnitOfWork(final Method method, final String glueLine, final Map<String, MethodDescriptor> unitOfWorkMap) {
        if (isValidMethod(method)) {
            unitOfWorkMap
                    .put(glueLine, new RegexMethodDescriptorImpl(glueLine, method, 0, hasStorageParameter(method)));
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
                final Object tmpDefinition = reflectionHelper.getInstanceForClass(method.getDeclaringClass());
                //TODO parse glueline for duration
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
