package org.craftsmenlabs.gareth.rest.v1.assembler;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v1.entity.Experiment;

import java.util.Optional;

/**
 * Created by hylke on 28/08/15.
 */
public class ExperimentAssembler implements Assembler<ExperimentContext, Experiment> {

    @Override
    public Experiment assembleOutbound(final ExperimentContext inbound) {
        Experiment experiment = null;
        if (Optional.ofNullable(inbound).isPresent()) {
            experiment = new Experiment();
            experiment.setBaselineGlueLine(inbound.getBaselineGlueLine());
            experiment.setAssumeGlueLine(inbound.getAssumeGlueLine());
            experiment.setTimeGlueLine(inbound.getTimeGlueLine());
            experiment.setSuccessGlueLine(inbound.getSuccessGlueLine());
            experiment.setFailureGlueLine(inbound.getFailureGlueLine());
            experiment.setBaselineExecution(inbound.getBaselineRun());
            experiment.setAssumeExecution(inbound.getAssumeRun());
            experiment.setSuccessExecution(inbound.getSuccessRun());
            experiment.setFailureExecution(inbound.getFailureRun());

        }
        return experiment;
    }

    @Override
    public ExperimentContext assembleInbound(final Experiment outbound) {
        throw new UnsupportedOperationException("Experiment cannot be assembled inbound");
    }
}
