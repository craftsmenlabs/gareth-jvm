package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.rest.RestService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by hylke on 19/08/15.
 */
public class RestServiceImpl implements RestService {


    private final int portNumber;

    private final ExperimentEngine experimentEngine;

    private RestServiceImpl(final Builder builder) {
        this.portNumber = builder.portNumber;
        this.experimentEngine = builder.experimentEngine;
    }

    @Override
    public void start() throws Exception {
        final URI baseUri = UriBuilder.fromUri("http://localhost/").port(portNumber).build();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, new GarethApplication(experimentEngine));
        server.start();
    }

    public static class Builder {
        private int portNumber = 8080;

        private ExperimentEngine experimentEngine;

        public Builder setExperimentEngine(final ExperimentEngine experimentEngine) {
            this.experimentEngine = experimentEngine;
            return this;
        }


        public RestService build() {
            return new RestServiceImpl(this);
        }
    }
}
