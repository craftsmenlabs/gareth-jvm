package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class RestServiceImpl {

    private final int portNumber;

    private final String uri;

    private final ExperimentEngineImpl experimentEngine;

    private RestServiceImpl(final Builder builder) {
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

        private ExperimentEngineImpl experimentEngine;

        public Builder setExperimentEngine(final ExperimentEngineImpl experimentEngine) {
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

        public RestServiceImpl build() {
            return new RestServiceImpl(this);
        }
    }
}
