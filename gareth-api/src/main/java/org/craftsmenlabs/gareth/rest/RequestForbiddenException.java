package org.craftsmenlabs.gareth.rest;

import org.springframework.web.client.HttpClientErrorException;

/**
 * Used when an authenticated user tries to access a resource to which she lacks the appropriate permissions.<br>
 * Not to be confused with HTTP 401 Unauthorized. This is given when authentication information is missing or invalid.
 */
@SuppressWarnings("serial")
public class RequestForbiddenException extends RestException {

    public RequestForbiddenException(String resource) {
        super("Unauthorized or access to resource forbidden: " + resource);
    }

    public RequestForbiddenException(String resource, final HttpClientErrorException cee) {
        super(cee, "Unauthorized or access to resource forbidden: " + resource);
    }
}
