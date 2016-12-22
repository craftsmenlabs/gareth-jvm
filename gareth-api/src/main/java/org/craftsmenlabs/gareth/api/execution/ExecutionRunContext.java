package org.craftsmenlabs.gareth.api.execution;


public interface ExecutionRunContext {

    void storeString(String key, String value);

    void storeLong(String key, long value);

    void storeDouble(String key, double value);

    void storeBoolean(String key, boolean value);

    String getString(String key);

    long getLong(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    ExecutionResult toExecutionResult(ExecutionStatus running);
}
