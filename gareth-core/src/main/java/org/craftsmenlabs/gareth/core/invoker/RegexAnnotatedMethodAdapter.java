package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexAnnotatedMethodAdapter {

    private final List<Class<?>> parameters = new ArrayList<>();
    private Pattern pattern;
    private Method method;

    public RegexAnnotatedMethodAdapter(final Method method, String pattern) {
        this.method = method;
        parseMethod();
        setPattern(pattern);
    }

    public Method getMethod() {
        return this.method;
    }

    public void invokeWith(Object receiver, List<Object> params) {
        //if (params == null || params.size() != parameters.size())
        //    throw new IllegalArgumentException("Parameter list should be non-null and size " + parameters.size());
        try {
            method.invoke(receiver, params.toArray(new Object[0]));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new GarethInvocationException(e);
        }
    }

    private void parseMethod() {
        for (Parameter parameter : method.getParameters()) {
            Class<?> cls = parameter.getType();
            if (parameter.getParameterizedType() != Storage.class) {
                if (!isValidType(cls)) {
                    throw new IllegalStateException("Parameter type " + cls + " is not supported");
                }
                parameters.add(cls);
            }
        }
    }

    private boolean isValidType(Type type) {
        return type.getTypeName().equals("java.lang.String") || type.getTypeName().equals("int") || type.getTypeName()
                                                                                                        .equals("long") || type
                .getTypeName().equals("double");
    }

    public List<Class<?>> getNonStorageParameters() {
        return parameters.stream().filter((p) -> p != Storage.class).collect(Collectors.toList());
    }

    public String getPattern() {
        return pattern.pattern();
    }

    private void setPattern(String regex) {
        try {
            pattern = Pattern.compile(regex);
        } catch (Exception e) {
            throw new GarethDefinitionParseException(e);
        }
    }

    public List<Object> getArgumentValuesFromInputString(final String input) {
        final List<String> parametersFromPattern = getParametersFromPattern(input.trim());
        final List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < parametersFromPattern.size(); i++) {
            Class<?> cls = getNonStorageParameters().get(i);
            parameters.add(getValueFromString(cls, parametersFromPattern.get(i)));
        }
        return parameters;
    }

    private Object getValueFromString(final Class<?> cls, final String stringVal) {
        if (cls == String.class) {
            return stringVal;
        }
        if (cls == Integer.TYPE) {
            return Integer.valueOf(stringVal);
        }
        if (cls == Long.TYPE) {
            return Long.valueOf(stringVal);
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(stringVal);
        }
        return null;
    }

    private List<String> getParametersFromPattern(final String s) {
        final List<String> output = new ArrayList<>();
        final Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Input string " + s + " could not be matched against pattern " + getPattern());
        }
        final int groupCount = matcher.groupCount();
        final int expectedParameters = getNonStorageParameters().size();
        if (groupCount != expectedParameters) {
            throw new IllegalArgumentException("Input string " + s + " must have " + expectedParameters + " parameters.");
        }
        for (int i = 1; i <= groupCount; i++) {
            output.add(matcher.group(i));
        }
        return output;
    }


}
