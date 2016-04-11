package org.craftsmenlabs.gareth.core.registry;

import org.craftsmenlabs.gareth.api.exception.GarethAlreadyKnownExperimentException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by hylke on 11/08/15.
 */
public class ExperimentRegistryImplTest {

    private ExperimentRegistryImpl experimentRegistry;

    @Mock
    private Experiment mockExperiment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        experimentRegistry = new ExperimentRegistryImpl();
        experimentRegistry.getExperiments().put("mockExperiment", mockExperiment);


    }

    @Test
    public void testAddExperiment() throws Exception {
        final Experiment experiment = new Experiment();
        experimentRegistry.addExperiment("experiment", experiment);
        assertEquals(2, experimentRegistry.getExperiments().size());
    }

    @Test
    public void testAddExperimentWithDuplicateExperimentName() throws Exception {
        try {
            final Experiment experiment = new Experiment();
            experimentRegistry.addExperiment("experiment", experiment);
            experimentRegistry.addExperiment("experiment", experiment);
            fail("Should not reach this point");
        } catch (final GarethAlreadyKnownExperimentException e) {
            assertEquals("Experiment with name 'experiment' already known", e.getMessage());
        }
    }

    @Test
    public void testGetExperiment() throws Exception {
        final Experiment experiment = experimentRegistry.getExperiment("mockExperiment");
        assertNotNull(experiment);
    }

    @Test
    public void testGetUnknownExperiment() throws Exception {
        try {
            experimentRegistry.getExperiment("unknown");
            fail("Should not reach this point");
        } catch (final GarethUnknownExperimentException e) {
            assertEquals("Experiment 'unknown' unknown", e.getMessage());
        }
    }

    @Test
    public void testGetAllExperiments() {
        final List<Experiment> experimentList = experimentRegistry.getAllExperiments();
        assertTrue(experimentList instanceof ArrayList);
        assertEquals(1, experimentList.size());
    }
}