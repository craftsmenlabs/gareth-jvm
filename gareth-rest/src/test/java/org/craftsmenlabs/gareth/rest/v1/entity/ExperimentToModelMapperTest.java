package org.craftsmenlabs.gareth.rest.v1.entity;

import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentToModelMapperTest {

    @Test
    public void testFullMapping() {
        Experiment experiment = new Experiment();
        experiment.setAssumeGlueLine("assume");
        experiment.setBaselineGlueLine("baseline");
        experiment.setExperimentName("Experiment");
        experiment.setFailureGlueLine("failure");
        experiment.setSuccessGlueLine("success");
        experiment.setTimeGlueLine("time");
        org.craftsmenlabs.gareth.api.model.Experiment mapped = new ExperimentToModelMapper().map(experiment);
        assertThat(mapped.getExperimentName()).isEqualTo("Experiment");
        assertThat(mapped.getAssumptionBlockList()).hasSize(1);
        AssumptionBlock block = mapped.getAssumptionBlockList().get(0);
        assertThat(block.getAssumption()).isEqualTo("assume");
        assertThat(block.getBaseline()).isEqualTo("baseline");
        assertThat(block.getFailure()).isEqualTo("failure");
        assertThat(block.getSuccess()).isEqualTo("success");
        assertThat(block.getTime()).isEqualTo("time");

    }
}