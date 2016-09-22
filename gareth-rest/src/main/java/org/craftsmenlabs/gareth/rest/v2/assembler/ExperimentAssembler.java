package org.craftsmenlabs.gareth.rest.v2.assembler;

import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Link;
import java.util.Collections;
import java.util.Optional;

@Component
public class ExperimentAssembler implements Assembler<ExperimentContext, Experiment> {

    @Override
    public Experiment assembleOutbound(final ExperimentContext inbound) {
        Experiment experiment = null;
        if (Optional.ofNullable(inbound).isPresent()) {
            experiment = new Experiment();
            experiment.setHash(inbound.getHash());
            experiment.setExperimentName(inbound.getExperimentName());
            experiment.setBaselineGlueLine(inbound.getBaselineGlueLine());
            experiment.setAssumeGlueLine(inbound.getAssumeGlueLine());
            experiment.setTimeGlueLine(inbound.getTimeGlueLine());
            experiment.setSuccessGlueLine(inbound.getSuccessGlueLine());
            experiment.setFailureGlueLine(inbound.getFailureGlueLine());
            experiment.setLinks(Collections.singletonList(Link.fromUri("http://localhost:8001/v2/experimentruns/" + inbound.getHash() + ".json").build()));
        }
        return experiment;
    }

    @Override
    public ExperimentContext assembleInbound(final Experiment outbound) {
        throw new UnsupportedOperationException("Experiment cannot be assembled inbound");
    }
}
