package org.craftsmenlabs.gareth.rest.v1.entity;

import org.craftsmenlabs.gareth.api.model.AssumptionBlock;

import java.util.Arrays;

public class ExperimentToModelMapper {
    public org.craftsmenlabs.gareth.api.model.Experiment map(final Experiment experiment) {
        org.craftsmenlabs.gareth.api.model.Experiment model = new org.craftsmenlabs.gareth.api.model.Experiment();
        model.setWeight(experiment.getWeight());
        model.setExperimentName(experiment.getExperimentName());
        AssumptionBlock block = new AssumptionBlock();
        block.setBaseline(experiment.getBaselineGlueLine());
        block.setAssumption(experiment.getAssumeGlueLine());
        block.setFailure(experiment.getFailureGlueLine());
        block.setSuccess(experiment.getSuccessGlueLine());
        block.setTime(experiment.getTimeGlueLine());
        model.setAssumptionBlockList(Arrays.asList(block));
        return model;
    }
}
