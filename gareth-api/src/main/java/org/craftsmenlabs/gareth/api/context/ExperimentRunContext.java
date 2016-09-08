package org.craftsmenlabs.gareth.api.context;

import org.craftsmenlabs.gareth.api.storage.Storage;

import java.time.LocalDateTime;


public interface ExperimentRunContext {

    String getHash();

    boolean hasFailures();

    boolean isRunning();

    boolean isFinished();

    void setFinished(final boolean finished);

    LocalDateTime getBaselineRun();

    void setBaselineRun(final LocalDateTime localDateTime);

    LocalDateTime getAssumeRun();

    void setAssumeRun(final LocalDateTime localDateTime);

    LocalDateTime getSuccessRun();

    void setSuccessRun(final LocalDateTime localDateTime);

    LocalDateTime getFailureRun();

    void setFailureRun(final LocalDateTime localDateTime);

    Storage getStorage();

    ExperimentPartState getBaselineState();

    void setBaselineState(final ExperimentPartState baselineState);

    ExperimentPartState getAssumeState();

    void setAssumeState(final ExperimentPartState assumeState);

    ExperimentPartState getSuccessState();

    void setSuccessState(final ExperimentPartState successState);

    ExperimentPartState getFailureState();

    void setFailureState(final ExperimentPartState failureState);

    ExperimentContext getExperimentContext();

}
