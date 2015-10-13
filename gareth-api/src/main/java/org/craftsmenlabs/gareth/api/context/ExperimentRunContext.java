package org.craftsmenlabs.gareth.api.context;

import org.craftsmenlabs.gareth.api.storage.Storage;

import java.time.LocalDateTime;

/**
 * Created by hylke on 13/10/15.
 */
public interface ExperimentRunContext {

    String getHash();

    boolean hasFailures();

    boolean isRunning();

    boolean isFinished();

    LocalDateTime getBaselineRun();

    LocalDateTime getAssumeRun();

    LocalDateTime getSuccessRun();

    LocalDateTime getFailureRun();

    void setBaselineRun(final LocalDateTime localDateTime);

    void setAssumeRun(final LocalDateTime localDateTime);

    void setSuccessRun(final LocalDateTime localDateTime);

    void setFailureRun(final LocalDateTime localDateTime);

    void setFinished(final boolean finished);

    Storage getStorage();

    ExperimentPartState getBaselineState();

    ExperimentPartState getAssumeState();

    ExperimentPartState getSuccessState();

    ExperimentPartState getFailureState();

    void setBaselineState(final ExperimentPartState baselineState);

    void setAssumeState(final ExperimentPartState assumeState);

    void setSuccessState(final ExperimentPartState successState);

    void setFailureState(final ExperimentPartState failureState);

    ExperimentContext getExperimentContext();

}
