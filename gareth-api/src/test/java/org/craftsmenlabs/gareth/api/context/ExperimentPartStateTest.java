package org.craftsmenlabs.gareth.api.context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hylke on 18/09/15.
 */
public class ExperimentPartStateTest {

    @Test
    public void testExperimentPartStateValues() {
        assertEquals(5, ExperimentPartState.values().length);
    }

    @Test
    public void testGetNameNON_EXISTENT() throws Exception {
        assertEquals("non_existent", ExperimentPartState.NON_EXISTENT.getName());
    }

    @Test
    public void testGetNameOPEN() throws Exception {
        assertEquals("open", ExperimentPartState.OPEN.getName());
    }

    @Test
    public void testGetNameRUNNING() throws Exception {
        assertEquals("running", ExperimentPartState.RUNNING.getName());
    }

    @Test
    public void testGetNameFINISHED() throws Exception {
        assertEquals("finished", ExperimentPartState.FINISHED.getName());
    }

    @Test
    public void testGetNameERROR() throws Exception {
        assertEquals("error", ExperimentPartState.ERROR.getName());
    }
}