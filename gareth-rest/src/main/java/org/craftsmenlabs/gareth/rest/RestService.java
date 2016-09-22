package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class RestService {

    private final int portNumber;

    private final String uri;

    private final ExperimentEngine experimentEngine;

    private RestService(final Builder builder) {
        this.portNumber = builder.portNumber;
        this.uri = builder.uri;
        this.experimentEngine = builder.experimentEngine;
    }

    public void start() throws Exception {
        final URI baseUri = UriBuilder.fromUri(uri).port(portNumber).build();
        final HttpServer server = GrizzlyHttpServerFactory
                .createHttpServer(baseUri, new GarethApplication(experimentEngine));
        server.start();
    }

    public static class Builder {
        private int portNumber = 8080;

        private String uri = "http://localhost/";

        private ExperimentEngine experimentEngine;

        public Builder setExperimentEngine(final ExperimentEngine experimentEngine) {
            this.experimentEngine = experimentEngine;
            return this;
        }

        public Builder setPortNumber(final int portNumber) {
            this.portNumber = portNumber;
            return this;
        }

        public Builder setURI(final String uri) {
            this.uri = uri;
            return this;
        }

        public RestService build() {
            return new RestService(this);
        }
    }
}
