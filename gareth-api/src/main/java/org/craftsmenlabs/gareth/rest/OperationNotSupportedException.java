package org.craftsmenlabs.gareth.rest;

@SuppressWarnings("serial")
public class OperationNotSupportedException extends RestException {

    public OperationNotSupportedException(String id) {
        super(String.format("Operation is not supported: %s", id));
    }

    public OperationNotSupportedException(String id, Throwable source) {
        super(source, String.format("Operation is not supported: %s", id));
    }

}
