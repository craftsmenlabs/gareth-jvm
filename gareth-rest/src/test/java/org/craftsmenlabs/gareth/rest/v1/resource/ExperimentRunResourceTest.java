package org.craftsmenlabs.gareth.rest.v1.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ExperimentRunResourceTest {

    @Mock
    private ExperimentEngine experimentEngine;

    @InjectMocks
    private ExperimentRunResource experimentRunResource;

    @Before
    public void before() throws Exception {
        experimentRunResource = new ExperimentRunResource();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGet() throws Exception {
        final Response response = experimentRunResource.get("hash");
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

}