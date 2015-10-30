package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.storage.Storage;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


public class RegexAnnotatedMethodAdapter {

    Pattern pattern;
    Method method;
    List<Class<?>> parameters = new ArrayList<>();

    public RegexAnnotatedMethodAdapter(final Method method) {
        this.method = method;
        parseMethod();
        getGlueLineTextFromAnnotation();
    }

    private void getGlueLineTextFromAnnotation() {
        ofNullable(method.getAnnotation(Baseline.class)).ifPresent(baseline -> {
            setPattern(baseline.glueLine());
        });
        ofNullable(method.getAnnotation(Assume.class)).ifPresent(assume -> {
            setPattern(assume.glueLine());
        });
        ofNullable(method.getAnnotation(Success.class)).ifPresent(success -> {
            setPattern(success.glueLine());
        });
        ofNullable(method.getAnnotation(Failure.class)).ifPresent(failure -> {
            setPattern(failure.glueLine());
        });
        ofNullable(method.getAnnotation(Time.class)).ifPresent(time -> {
            setPattern(time.glueLine());
        });
    }

    private void setPattern(String regex) {
        pattern = Pattern.compile(regex);
    }


    public Method getMethod() {
        return this.method;
    }


    private void parseMethod() {
        for (Parameter parameter : method.getParameters()) {
            Class<?> cls = parameter.getType();
            if (parameter.getParameterizedType() != Storage.class) {
                if (!isValidType(cls))
                    throw new IllegalStateException("Parameter type " + cls + " is not supported");
                parameters.add(cls);
            }
        }
    }

    private boolean isValidType(Type type) {
        return type.getTypeName().equals("java.lang.String")
                || type.getTypeName().equals("int")
                || type.getTypeName().equals("long")
                || type.getTypeName().equals("double");
    }

    public List<Class<?>> getNonStorageParameters() {
        return parameters.stream().filter((p) -> p != Storage.class).collect(Collectors.toList());
    }

    public String getPattern() {
        return pattern.pattern();
    }

    public List<Object> getParametersFromInputString(final String input) {
        List<String> parametersFromPattern = getParametersFromPattern(input.trim());
        List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < parametersFromPattern.size(); i++) {
            Class<?> cls = getNonStorageParameters().get(i);
            parameters.add(getValueFromString(cls, parametersFromPattern.get(i)));
        }
        return parameters;
    }

    Object getValueFromString(Class<?> cls, String stringVal) {
        if (cls == String.class)
            return stringVal;
        if (cls == Integer.TYPE)
            return Integer.valueOf(stringVal);
        if (cls == Long.TYPE)
            return Long.valueOf(stringVal);
        if (cls == Double.TYPE)
            return Double.valueOf(stringVal);
        return null;
    }

    List<String> getParametersFromPattern(String s) {
        List<String> output = new ArrayList<>();
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Input string " + s + " could not be matched against pattern " + getPattern());
        }
        int groupCount = matcher.groupCount();
        int expectedParameters = getNonStorageParameters().size();
        if (groupCount != expectedParameters) {
            throw new IllegalArgumentException("Input string " + s + " must have " + expectedParameters + " parameters.");
        }
        for (int i = 1; i <= groupCount; i++) {
            output.add(matcher.group(i));
        }
        return output;
    }

}
