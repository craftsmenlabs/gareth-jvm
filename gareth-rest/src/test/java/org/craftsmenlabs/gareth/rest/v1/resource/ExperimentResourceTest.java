package org.craftsmenlabs.gareth.rest.v1.resource;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hylke on 18/09/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExperimentResourceTest {

    @Mock
    private ExperimentEngine experimentEngine;

    @InjectMocks
    private ExperimentResource experimentResource;

    @Before
    public void before() throws Exception {
        experimentResource = new ExperimentResource();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGet() throws Exception {
        final Response response = experimentResource.get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }
}