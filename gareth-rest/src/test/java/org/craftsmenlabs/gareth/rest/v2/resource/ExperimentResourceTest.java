package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Injectable;
import mockit.Tested;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class ExperimentResourceTest {

    @Injectable
    private ExperimentEngineImpl experimentEngine;

    @Tested
    private ExperimentResource experimentResource;

    @Test
    public void testGet() throws Exception {
        final Response response = experimentResource.get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }
}