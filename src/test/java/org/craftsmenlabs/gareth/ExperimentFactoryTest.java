package org.craftsmenlabs.gareth;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by hylke on 04/08/15.
 */
public class ExperimentFactoryTest {

    @Test
    public void testExperiment1() {
        final Experiment experiment = parseTestExperiment("experiment-0001.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock =  new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setAssumption("5 days");

        validateAssumptionBlock(assumptionBlock,expectedAssumptionBlock);

    }

    @Test
    public void testExperiment2() {
        final Experiment experiment = parseTestExperiment("experiment-0002.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (multiple assumptions)", experiment.getExperimentName());
        assertEquals(2, experiment.getAssumptionBlockList().size());
        // Validate assumption block 1
        final AssumptionBlock assumptionBlock1 = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock1 =  new AssumptionBlock();
        expectedAssumptionBlock1.setBaseline("Failed logins are read from log1");
        expectedAssumptionBlock1.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%1");
        expectedAssumptionBlock1.setTime("5 days1");

        validateAssumptionBlock(assumptionBlock1,expectedAssumptionBlock1);

        // Validate assumptionblock2
        final AssumptionBlock assumptionBlock2 = experiment.getAssumptionBlockList().get(1);
        final AssumptionBlock expectedAssumptionBlock12 =  new AssumptionBlock();
        expectedAssumptionBlock12.setBaseline("Failed logins are read from log2");
        expectedAssumptionBlock12.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%2");
        expectedAssumptionBlock12.setTime("5 days2");

        validateAssumptionBlock(assumptionBlock2,expectedAssumptionBlock12);


    }

    private Experiment parseTestExperiment(final String fileName) {
        final InputStream fileInputStream = getClass().getResourceAsStream("/experiments/" + fileName);

        try {


            return new ExperimentFactory().buildExperiment(fileInputStream);


        } catch (Exception e) {
            fail("Should not reach this point");
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        throw new IllegalStateException("Should not reach this point");
    }

    private void validateAssumptionBlock(final AssumptionBlock assumptionBlock,final AssumptionBlock expectedAssumptionBlock) {

        assertEquals(expectedAssumptionBlock.getBaseline(), assumptionBlock.getBaseline());
        assertEquals(expectedAssumptionBlock.getAssumption(), assumptionBlock.getAssumption());
        assertEquals(expectedAssumptionBlock.getTime(), assumptionBlock.getTime());
        assertEquals(expectedAssumptionBlock.getSuccess(), assumptionBlock.getSuccess());
        assertEquals(expectedAssumptionBlock.getFailure(), assumptionBlock.getFailure());
    }
}
