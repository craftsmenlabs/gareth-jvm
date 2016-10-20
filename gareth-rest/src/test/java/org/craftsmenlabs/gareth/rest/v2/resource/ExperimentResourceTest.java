package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class ExperimentResourceTest {

    @Injectable
    private ExperimentEngine experimentEngine;

    @Tested
    private ExperimentResource experimentResource;

    @Test
    public void testGet(@Injectable ExperimentContext experimentContext) throws Exception {
        new Expectations() {{
            experimentEngine.getExperimentContexts();
            result = Arrays.asList(experimentContext);
        }};
        final List<Experiment> response = experimentResource.get();
        assertNotNull(response);
        assertThat(response).hasSize(1);
    }
}
