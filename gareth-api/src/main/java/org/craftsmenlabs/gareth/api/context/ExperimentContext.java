package org.craftsmenlabs.gareth.api.context;

import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;

import java.time.Duration;

public interface ExperimentContext {

    String getHash();

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

    boolean hasStorage();

}
