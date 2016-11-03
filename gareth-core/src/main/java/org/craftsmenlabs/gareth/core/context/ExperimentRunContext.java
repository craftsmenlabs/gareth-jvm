package org.craftsmenlabs.gareth.core.context;

import lombok.Getter;
import lombok.Setter;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;

import java.time.LocalDateTime;

/**
 * Contains information about an experiment run, i.e. the lifecycle state and start time of each baseline
 */
@Getter
public class ExperimentRunContext {

    private final ExperimentContext experimentContext;

    private final DefaultStorage storage;

    @Setter
    private ExperimentPartState baselineState;
    @Setter
    private ExperimentPartState assumeState;
    @Setter
    private ExperimentPartState successState;
    @Setter
    private ExperimentPartState failureState;

    @Setter
    private LocalDateTime baselineRun;
    @Setter
    private LocalDateTime assumeRun;
    @Setter
    private LocalDateTime successRun;
    @Setter
    private LocalDateTime failureRun;

    private boolean finished;

    private ExperimentRunContext(final Builder builder) {
        this.experimentContext = builder.experimentContext;
        this.storage = builder.storage;

        // State
        this.baselineState = builder.baselineState;
        this.assumeState = builder.assumeState;
        this.successState = builder.successState;
        this.failureState = builder.failureState;
    }

    public String getHash() {
        return experimentContext.getHash();
    }

    public boolean hasFailures() {
        return null != failureRun;
    }

    public boolean isRunning() {
        return (null != baselineRun || null != assumeRun)
                && !(null != successRun || null != failureRun);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public static class Builder {
        private ExperimentContext experimentContext;
        private DefaultStorage storage;

        private ExperimentPartState baselineState = ExperimentPartState.NON_EXISTENT;
        private ExperimentPartState assumeState = ExperimentPartState.NON_EXISTENT;
        private ExperimentPartState successState = ExperimentPartState.NON_EXISTENT;
        private ExperimentPartState failureState = ExperimentPartState.NON_EXISTENT;

        public Builder(final ExperimentContext experimentContext, final DefaultStorage storage) {
            this.experimentContext = experimentContext;
            this.storage = storage;
        }

        public ExperimentRunContext build() {
            if (experimentContext == null) {
                throw new IllegalStateException("Cannot build experiment run context without experiment context");
            }

            if (experimentContext.getBaseline() != null) baselineState = ExperimentPartState.OPEN;
            if (experimentContext.getAssume() != null) assumeState = ExperimentPartState.OPEN;
            if (experimentContext.getSuccess() != null) successState = ExperimentPartState.OPEN;
            if (experimentContext.getFailure() != null) failureState = ExperimentPartState.OPEN;

            return new ExperimentRunContext(this);
        }
    }
}
