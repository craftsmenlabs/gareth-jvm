package org.craftsmenlabs.gareth.rest;

import org.springframework.web.client.HttpClientErrorException;

@SuppressWarnings("serial")
public class NotFoundException extends RestException {

    public NotFoundException(String resource) {
        super("Resource not found: " + resource);
    }

    public NotFoundException(final String text, final HttpClientErrorException cee) {
        super(cee, text);
    }
}
