package org.craftsmenlabs.gareth.rest.v2.assembler;

import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentRun;


public class ExperimentRunAssembler implements Assembler<ExperimentRunContextImpl, ExperimentRun> {

    @Override
    public ExperimentRun assembleOutbound(final ExperimentRunContextImpl inbound) {
        ExperimentRun experimentRun = null;
        if (inbound != null) {
            experimentRun = new ExperimentRun();
            // States
            experimentRun.setBaselineState(inbound.getBaselineState().getName());
            experimentRun.setAssumeState(inbound.getAssumeState().getName());
            experimentRun.setSuccessState(inbound.getSuccessState().getName());
            experimentRun.setFailureState(inbound.getFailureState().getName());
            // Run times
            experimentRun.setBaselineExecution(inbound.getBaselineRun());
            experimentRun.setAssumeExecution(inbound.getAssumeRun());
            experimentRun.setSuccessExecution(inbound.getSuccessRun());
            experimentRun.setFailureExecution(inbound.getFailureRun());
        }
        return experimentRun;
    }

    @Override
    public ExperimentRunContextImpl assembleInbound(final ExperimentRun outbound) {
        throw new UnsupportedOperationException("Experiment run cannot be assembled inbound");
    }
}
