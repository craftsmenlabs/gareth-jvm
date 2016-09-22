package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Injectable;
import mockit.Tested;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentRunResource;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ExperimentRunResourceTest {

    @Injectable
    private ExperimentEngineImpl experimentEngine;

    @Tested
    private ExperimentRunResource experimentRunResource;

    @Test
    public void testGet() throws Exception {
        final Response response = experimentRunResource.get("hash");
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

}