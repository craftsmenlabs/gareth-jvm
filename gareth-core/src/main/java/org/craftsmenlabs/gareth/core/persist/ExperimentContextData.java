package org.craftsmenlabs.gareth.core.persist;

import lombok.Data;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by hylke on 23/09/15.
 */
@Data
class ExperimentContextData implements Serializable {
    private String hash;
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;
    private ExperimentPartState baselineState, assumeState, successState, failureState;
}
