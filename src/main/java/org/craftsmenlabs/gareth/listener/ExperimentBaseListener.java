package org.craftsmenlabs.gareth.listener;

import lombok.Getter;
import org.craftsmenlabs.gareth.GarethBaseListener;
import org.craftsmenlabs.gareth.GarethParser;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;

/**
 * Created by hylke on 04/08/15.
 */
public class ExperimentBaseListener extends GarethBaseListener {

    @Getter
    private final Experiment experiment;

    public ExperimentBaseListener() {
        this.experiment = new Experiment();
    }

    @Override
    public void enterAssumptionBlock(GarethParser.AssumptionBlockContext ctx) {
        experiment.getAssumptionBlockList().add(new AssumptionBlock());
    }


    @Override
    public void exitExperiment(GarethParser.ExperimentContext ctx) {
        experiment.setExperimentName(cleanupInput(ctx.expirementName.getText()));
    }

    @Override
    public void exitBaseline(GarethParser.BaselineContext ctx) {
        getNewestAssumptionBlock().setBaseline(cleanupInput(ctx.baselineGlueLine.getText()));
    }

    @Override
    public void exitAssumption(GarethParser.AssumptionContext ctx) {
        getNewestAssumptionBlock().setAssumption(cleanupInput(ctx.assumptionGlueLine.getText()));
    }

    @Override
    public void exitSuccess(GarethParser.SuccessContext ctx) {
        getNewestAssumptionBlock().setSuccess(cleanupInput(ctx.successGlueLine.getText()));
    }

    @Override
    public void exitFailure(GarethParser.FailureContext ctx) {
        getNewestAssumptionBlock().setFailure(cleanupInput(ctx.failureGlueLine.getText()));
    }

    @Override
    public void exitTime(GarethParser.TimeContext ctx) {
        getNewestAssumptionBlock().setTime(cleanupInput(ctx.timeGlueLine.getText()));
    }

    private AssumptionBlock getNewestAssumptionBlock() {
        return experiment.getAssumptionBlockList().get(experiment.getAssumptionBlockList().size() - 1);
    }

    private String cleanupInput(final String input) {
        String returnValue = input;
        if (input != null) {
            returnValue = input.trim();
        }
        return returnValue;
    }
}
