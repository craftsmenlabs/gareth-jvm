package org.craftsmenlabs.gareth.api.context;

import java.lang.reflect.Method;
import java.time.Duration;

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

    Method getAssume();

    Method getBaseline();

    Duration getTime();

    Method getSuccess();

    Method getFailure();

    boolean isValid();

    boolean hasFailures();

    boolean isRunning();

    boolean isFinished();

}
