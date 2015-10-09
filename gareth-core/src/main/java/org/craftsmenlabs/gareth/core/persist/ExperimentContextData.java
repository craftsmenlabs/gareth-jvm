package org.craftsmenlabs.gareth.core.persist;

import lombok.Data;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by hylke on 23/09/15.
 */
@Data
class ExperimentContextData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hash;
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;
    private ExperimentPartState baselineState, assumeState, successState, failureState;
    private Storage storage;
}
