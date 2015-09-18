package org.craftsmenlabs.gareth.api.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class ExperimentTest {

    private Experiment experiment;

    @Before
    public void before() {
        experiment = new Experiment();
    }

    @Test
    public void testGetExperimentName() throws Exception {
        assertNull(experiment.getExperimentName());
    }

    @Test
    public void testGetAssumptionBlockList() throws Exception {
        assertNotNull(experiment.getAssumptionBlockList());
        assertEquals(0, experiment.getAssumptionBlockList().size());
    }

    @Test
    public void testSetGetExperimentName() throws Exception {
        experiment.setExperimentName("experiment1");
        assertEquals("experiment1", experiment.getExperimentName());
    }

    @Test
    public void testSetGetAssumptionBlockList() throws Exception {
        final List<AssumptionBlock> otherAssumptionBlockList = new ArrayList<>();
        assertNotSame(otherAssumptionBlockList, experiment.getAssumptionBlockList());
        experiment.setAssumptionBlockList(otherAssumptionBlockList);
        assertSame(otherAssumptionBlockList, experiment.getAssumptionBlockList());
    }
}