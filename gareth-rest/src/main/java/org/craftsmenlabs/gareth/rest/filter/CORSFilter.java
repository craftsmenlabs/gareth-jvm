package org.craftsmenlabs.gareth.rest.filter;



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
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        responseHeaders.add("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        responseHeaders.add("Access-Control-Max-Age", ACCESS_CONTROL_MAX_AGE_IN_SECONDS);
    }
}
