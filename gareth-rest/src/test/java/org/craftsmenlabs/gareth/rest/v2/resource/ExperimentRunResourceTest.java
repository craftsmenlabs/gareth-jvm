package org.craftsmenlabs.gareth.rest.v2.resource;

import mockit.Injectable;
import mockit.Tested;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentRun;
import org.craftsmenlabs.gareth.rest.v2.resources.ExperimentRunResource;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;


public class ExperimentRunResourceTest {

    @Injectable
    private ExperimentEngine experimentEngine;

    @Tested
    private ExperimentRunResource experimentRunResource;

    @Test
    public void testGet() throws Exception {
        final List<ExperimentRun> response = experimentRunResource.get("hash");
        assertNotNull(response);
    }
}
