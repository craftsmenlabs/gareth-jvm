package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;


public class RestServiceFactoryTest {


    private RestServiceFactory restServiceFactory;

    @Mock
    private ExperimentEngine mockExperimentEngine;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        restServiceFactory = new RestServiceFactory();
    }

    @Test
    public void testCreate() throws Exception {
        final RestService restService1 = restServiceFactory.create(mockExperimentEngine, 80);
        final RestService restService2 = restServiceFactory.create(mockExperimentEngine, 80);
        assertNotNull(restService1);
        assertNotNull(restService2);
        assertNotSame(restService1, restService2);
    }
}