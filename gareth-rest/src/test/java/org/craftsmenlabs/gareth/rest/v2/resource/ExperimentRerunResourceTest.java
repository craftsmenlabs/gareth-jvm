package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentRerunResource;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class ExperimentRerunResourceTest {

    @Injectable
    private ExperimentEngineImpl experimentEngine;

    @Injectable
    private ExperimentContextImpl experimentContext;

    @Tested
    private ExperimentRerunResource experimentRerunResource;

    @Test
    public void testRerunExperiment() throws Exception {
        final String hash = "hash";

        new Expectations(){{
            experimentEngine.findExperimentContextForHash(hash);
            result=experimentContext;
        }};

        final Response response = experimentRerunResource.rerunExperiment(hash);

        new Verifications(){{
            experimentEngine.planExperimentContext(experimentContext);
        }};

        assertEquals(202, response.getStatus());
    }

    @Test
    public void testRerunExperimentWithUnknownExperiment() throws Exception {
        final String hash = "hash";

        new Expectations(){{
            experimentEngine.findExperimentContextForHash(hash);
            result=new GarethUnknownExperimentException("b");
        }};

        final Response response = experimentRerunResource.rerunExperiment(hash);

        new Verifications(){{
            experimentEngine.planExperimentContext(experimentContext);
            maxTimes = 0;
        }};

        assertEquals(404, response.getStatus());
    }
}