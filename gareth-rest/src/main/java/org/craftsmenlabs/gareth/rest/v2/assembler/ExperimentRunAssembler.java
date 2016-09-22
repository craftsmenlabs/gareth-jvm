package org.craftsmenlabs.gareth.rest.v2.assembler;

import org.craftsmenlabs.gareth.core.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.entity.ExperimentRun;
import org.springframework.stereotype.Component;

@Component
public class ExperimentRunAssembler implements Assembler<ExperimentRunContext, ExperimentRun> {

    @Override
    public ExperimentRun assembleOutbound(final ExperimentRunContext inbound) {
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
    public ExperimentRunContext assembleInbound(final ExperimentRun outbound) {
        throw new UnsupportedOperationException("Experiment run cannot be assembled inbound");
    }
}
