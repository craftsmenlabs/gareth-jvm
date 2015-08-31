package org.craftsmenlabs.gareth.rest.v1.assembler;

import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.rest.v1.entity.Experiment;

import java.util.Optional;

/**
 * Created by hylke on 27/08/15.
 */
public class OutboundAssembler {

    public Experiment assembleExperiment(final ExperimentContext experimentContext) {
        Experiment experiment = null;
        if(Optional.ofNullable(experimentContext).isPresent()){
            experiment = new Experiment();

        }
        return experiment;
    }
}
