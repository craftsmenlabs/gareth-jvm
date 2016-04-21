package org.craftsmenlabs.gareth.rest.v1.media;

import javax.ws.rs.core.MediaType;

public class GarethMediaType extends MediaType {

    public static final MediaType APPLICATION_JSON_EXPERIMENTS_V1_TYPE = new MediaType("application", "vnd+org.craftsmenlabs.gareth.experiments-v1+json");
    public static final MediaType APPLICATION_JSON_EXPERIMENTRUNS_V1_TYPE = new MediaType("application", "vnd+org.craftsmenlabs.gareth.experimentruns-v1+json");
    public static final String APPLICATION_JSON_EXPERIMENTS_V1 = "application/vnd+org.craftsmenlabs.gareth.experiments-v1+json";
    public static final String APPLICATION_JSON_EXPERIMENTRUNS_V1 = "application/vnd+org.craftsmenlabs.gareth.experimentruns-v1+json";

}
