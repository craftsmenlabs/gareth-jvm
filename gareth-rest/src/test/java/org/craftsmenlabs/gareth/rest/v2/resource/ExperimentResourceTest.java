package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Injectable;
import mockit.Tested;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class ExperimentResourceTest {

    @Injectable
    private ExperimentEngine experimentEngine;

    @Tested
    private ExperimentResource experimentResource;

    @Test
    public void testGet() throws Exception {
        final List<Experiment> response = experimentResource.get();
        assertNotNull(response);
    }
}
