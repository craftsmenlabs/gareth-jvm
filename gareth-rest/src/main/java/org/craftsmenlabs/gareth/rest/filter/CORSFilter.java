package org.craftsmenlabs.gareth.rest.filter;

/**
 * Created by hylke on 11/09/15.
 */

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Singleton
@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        final int ACCESS_CONTROL_MAX_AGE_IN_SECONDS = 12 * 60 * 60;
        final MultivaluedMap<String, Object> responseHeaders = responseContext.getHeaders();

        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Accces-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseHeaders.add("Access-Control-Max-Age", ACCESS_CONTROL_MAX_AGE_IN_SECONDS);

    }
}
