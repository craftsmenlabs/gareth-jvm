package org.craftsmenlabs.gareth.core.factory;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ExperimentFactoryTest {

    @Test
    public void testExperiment1() {
        final Experiment experiment = parseTestExperiment("experiment-0001.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins", experiment.getExperimentName());
        assertEquals(8, experiment.getWeight());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testExperiment2() {
        final Experiment experiment = parseTestExperiment("experiment-0002.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (multiple assumptions)", experiment.getExperimentName());
        assertEquals(2, experiment.getAssumptionBlockList().size());
        assertEquals(0, experiment.getWeight());
        // Validate assumption block 1
        final AssumptionBlock assumptionBlock1 = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock1 = new AssumptionBlock();
        expectedAssumptionBlock1.setBaseline("Failed logins are read from log1");
        expectedAssumptionBlock1.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%1");
        expectedAssumptionBlock1.setTime("5 days1");

        validateAssumptionBlock(assumptionBlock1, expectedAssumptionBlock1);

        // Validate assumptionblock2
        final AssumptionBlock assumptionBlock2 = experiment.getAssumptionBlockList().get(1);
        final AssumptionBlock expectedAssumptionBlock12 = new AssumptionBlock();
        expectedAssumptionBlock12.setBaseline("Failed logins are read from log2");
        expectedAssumptionBlock12.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%2");
        expectedAssumptionBlock12.setTime("5 days2");

        validateAssumptionBlock(assumptionBlock2, expectedAssumptionBlock12);


    }

    @Test
    public void testExperiment3() {
        final Experiment experiment = parseTestExperiment("experiment-0003.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (with failure)", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");
        expectedAssumptionBlock.setFailure("this is a failure");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testExperiment4() {
        final Experiment experiment = parseTestExperiment("experiment-0004.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (with success)", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");
        expectedAssumptionBlock.setSuccess("this is a success");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testExperiment5() {
        final Experiment experiment = parseTestExperiment("experiment-0005.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (with success and failure)", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");
        expectedAssumptionBlock.setFailure("this is a failure");
        expectedAssumptionBlock.setSuccess("this is a success");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testExperiment6() {
        final Experiment experiment = parseTestExperiment("experiment-0006.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (with success and failure flipped)", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");
        expectedAssumptionBlock.setFailure("this is a failure");
        expectedAssumptionBlock.setSuccess("this is a success");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testExperiment7() {
        final Experiment experiment = parseTestExperiment("experiment-0007.experiment");


        assertNotNull(experiment);
        assertEquals("Reduce failed logins (A lot of new lines)", experiment.getExperimentName());
        assertEquals(1, experiment.getAssumptionBlockList().size());
        // Validate assumption block
        final AssumptionBlock assumptionBlock = experiment.getAssumptionBlockList().get(0);
        final AssumptionBlock expectedAssumptionBlock = new AssumptionBlock();
        expectedAssumptionBlock.setBaseline("Failed logins are read from log");
        expectedAssumptionBlock.setAssumption("By showing a caps-lock warning the failed logins are reduced by 5%");
        expectedAssumptionBlock.setTime("5 days");

        validateAssumptionBlock(assumptionBlock, expectedAssumptionBlock);

    }

    @Test
    public void testEmptyExperiment() {
        Assertions.assertThatThrownBy(() -> parseFailureExperiment("experiment-failure-0001.experiment"))
            .isInstanceOf(GarethExperimentParseException.class);
    }

    @Test
    public void testNullInputStreamExperiment() {
        try {
            new ExperimentFactory().buildExperiment(null);
        } catch (final Exception e) {
            assertTrue(e instanceof GarethExperimentParseException);
        }
    }

    private void parseFailureExperiment(final String fileName) throws Exception {
        final InputStream fileInputStream = getClass().getResourceAsStream("/experiments/" + fileName);

        try {
            new ExperimentFactory().buildExperiment(fileInputStream);
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
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

    private void validateAssumptionBlock(final AssumptionBlock assumptionBlock, final AssumptionBlock expectedAssumptionBlock) {

        assertEquals(expectedAssumptionBlock.getBaseline(), assumptionBlock.getBaseline());
        assertEquals(expectedAssumptionBlock.getAssumption(), assumptionBlock.getAssumption());
        assertEquals(expectedAssumptionBlock.getTime(), assumptionBlock.getTime());
        assertEquals(expectedAssumptionBlock.getSuccess(), assumptionBlock.getSuccess());
        assertEquals(expectedAssumptionBlock.getFailure(), assumptionBlock.getFailure());
    }
}
