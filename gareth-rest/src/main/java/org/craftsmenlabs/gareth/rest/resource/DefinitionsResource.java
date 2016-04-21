package org.craftsmenlabs.gareth.rest.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.v1.media.GarethMediaType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/definitions")
public class DefinitionsResource {

    @Inject
    private ExperimentEngine experimentEngine;
    private Map<String, Set<String>> glueLinesPerCategory;

    @PostConstruct
    public void init() {
        glueLinesPerCategory = experimentEngine.getDefinitionRegistry()
                                               .getGlueLinesPerCategory();
    }



    @Path("{key}/{value}")
    @GET
    @Produces({GarethMediaType.APPLICATION_JSON_EXPERIMENTS_V1, MediaType.APPLICATION_JSON})
    public Response getMatches(final @PathParam("key") String key, final @PathParam("value") String value) {
        if (!glueLinesPerCategory.containsKey(key)) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("final part of path must be baseline, assume, succcess, failure or time").build();
        }
        return produceResponse(getMatches(glueLinesPerCategory.get(key), value));
    }

    private Map<String, List<String>> getMatches(final Set<String> patterns, final String line) {
        if (line == null || line.length() < 3)
            return Collections.emptyMap();
        Map<String, List<String>> output = new HashMap<>();
        output.put("suggestions", getList(patterns, p -> p.contains(line) || matches(p, line)));
        output.put("matches", getList(patterns, p -> matches(p, line)));
        return output;
    }

    private List<String> getList(final Set<String> patterns, Predicate<String> filter) {
        return patterns.stream().filter(filter).map(p -> regexForNonTechies(p)).collect(Collectors.toList());
    }

    private String regexForNonTechies(final String pattern) {
        return pattern.replaceAll("^\\^", "").replaceAll("\\$$", "").replaceAll("\\(.+?\\)", "*");
    }

    private boolean matches(final String pattern, final String test) {
        Pattern compile = Pattern.compile(pattern);
        return compile.matcher(test).matches();
    }

    private Response produceResponse(Map<String, List<String>> matches) {
        return Response
                .status(200)
                .entity(new GenericEntity<Map<String, List<String>>>(matches) {
                })
                .build();
    }


}
