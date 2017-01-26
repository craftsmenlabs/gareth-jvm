package org.craftsmenlabs.gareth.rest;

@SuppressWarnings("serial")
public class BadRequestException extends RestException {

    public BadRequestException(String text) {
        super(text);
    }

    public BadRequestException(String text, Throwable source) {
        super(source, text);
    }

}
