package org.craftsmenlabs.gareth.api.context;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Experiment context definition
 * <p>
 * Created by hylke on 10/08/15.
 */
public interface ExperimentContext {

    String getExperimentName();

    String getAssumeGlueLine();

    String getBaselineGlueLine();

    String getTimeGlueLine();

    String getSuccessGlueLine();

    String getFailureGlueLine();

    MethodDescriptor getAssume();

    MethodDescriptor getBaseline();

    Duration getTime();

    MethodDescriptor getSuccess();

    MethodDescriptor getFailure();

    boolean isValid();

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

    boolean hasStorage();

    Storage getStorage();

}
