package org.craftsmenlabs.gareth.api.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class AssumptionBlockTest {

    private AssumptionBlock assumptionBlock;

    @Before
    public void setUp() throws Exception {
        assumptionBlock = new AssumptionBlock();
    }

    @Test
    public void testSetGetBaseline() throws Exception {
        assumptionBlock.setBaseline("baseline1");
        assertEquals("baseline1", assumptionBlock.getBaseline());
    }

    @Test
    public void testSetGetAssumption() throws Exception {
        assumptionBlock.setAssumption("assumption1");
        assertEquals("assumption1", assumptionBlock.getAssumption());
    }

    @Test
    public void testSetGetTime() throws Exception {
        assumptionBlock.setTime("time1");
        assertEquals("time1", assumptionBlock.getTime());
    }

    @Test
    public void testSetGetSuccess() throws Exception {
        assumptionBlock.setSuccess("success1");
        assertEquals("success1", assumptionBlock.getSuccess());
    }

    @Test
    public void testSetGetFailure() throws Exception {
        assumptionBlock.setFailure("failure1");
        assertEquals("failure1", assumptionBlock.getFailure());
    }

    @Test
    public void testGetBaseline() throws Exception {
        assertNull(assumptionBlock.getBaseline());
    }

    @Test
    public void testGetAssumption() throws Exception {
        assertNull(assumptionBlock.getAssumption());
    }

    @Test
    public void testGetTime() throws Exception {
        assertNull(assumptionBlock.getTime());
    }

    @Test
    public void testGetSuccess() throws Exception {
        assertNull(assumptionBlock.getSuccess());
    }

    @Test
    public void testGetFailure() throws Exception {
        assertNull(assumptionBlock.getFailure());
    }
}