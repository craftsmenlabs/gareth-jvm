package org.craftsmenlabs.gareth.core.context;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.storage.Storage;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by hylke on 10/08/15.
 */
@XmlRootElement
@Getter
@EqualsAndHashCode(of = {"hash"})
public class ExperimentContextImpl implements ExperimentContext {

    private final String hash;

    private final String experimentName;

    private final MethodDescriptor baseline, assume, success, failure;

    private final Duration time;

    private final String baselineGlueLine, assumeGlueLine, successGlueLine, failureGlueLine, timeGlueLine;

    private boolean finished;

    private final Storage storage;

    @Setter
    private ExperimentPartState baselineState, assumeState, successState, failureState;


    @Setter
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;

    private ExperimentContextImpl(final String hash, final Builder builder) {
        this.hash = hash;
        this.experimentName = builder.experimentName;
        // Gluelines
        this.baselineGlueLine = builder.assumptionBlock.getBaseline();
        this.assumeGlueLine = builder.assumptionBlock.getAssumption();
        this.timeGlueLine = builder.assumptionBlock.getTime();
        this.successGlueLine = builder.assumptionBlock.getSuccess();
        this.failureGlueLine = builder.assumptionBlock.getFailure();
        // Methods
        this.assume = builder.assume;
        this.baseline = builder.baseline;
        this.success = builder.success;
        this.failure = builder.failure;
        // State
        this.baselineState = builder.baselineState;
        this.assumeState = builder.assumeState;
        this.successState = builder.successState;
        this.failureState = builder.failureState;
        // Time
        this.time = builder.time;
        // Storage
        this.storage = builder.storage;

    }


    @Override
    public boolean isValid() {
        return null != baseline
                && null != assume
                && null != time;
    }

    @Override
    public boolean hasFailures() {
        return null != failureRun;
    }

    @Override
    public boolean isRunning() {
        return (null != baselineRun || null != assumeRun)
                && !(null != successRun || null != failureRun);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean hasStorage() {
        return getAssume().hasStorage()
                || getBaseline().hasStorage()
                || (null != getFailure() && getFailure().hasStorage())
                || (null != getSuccess() && getSuccess().hasStorage());
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public static class Builder {

        private final String experimentName;

        private final AssumptionBlock assumptionBlock;

        private MethodDescriptor baseline, assume, success, failure;

        private Duration time;

        private Storage storage = null;

        private ExperimentPartState baselineState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState assumeState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState successState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState failureState = ExperimentPartState.NON_EXISTENT;

        public Builder(final String experimentName, final AssumptionBlock assumptionBlock) {
            this.experimentName = experimentName;
            this.assumptionBlock = assumptionBlock;
        }

        public Builder setBaseline(final Optional<MethodDescriptor> baseline) {
            if (baseline.isPresent()) {
                this.baseline = baseline.get();
                this.baselineState = ExperimentPartState.OPEN;
            }
            return this;
        }

        public Builder setAssume(final Optional<MethodDescriptor> assume) {
            if (assume.isPresent()) {
                this.assume = assume.get();
                this.assumeState = ExperimentPartState.OPEN;
            }
            return this;
        }

        public Builder setSuccess(final Optional<MethodDescriptor> success) {
            if (success.isPresent()) {
                this.success = success.get();
                this.successState = ExperimentPartState.OPEN;
            }
            return this;
        }

        public Builder setFailure(final Optional<MethodDescriptor> failure) {
            if (failure.isPresent()) {
                this.failure = failure.get();
                this.failureState = ExperimentPartState.OPEN;
            }
            return this;
        }

        public Builder setTime(final Duration time) {
            this.time = time;
            return this;
        }

        public Builder setStorage(final Storage storage) {
            this.storage = storage;
            return this;
        }

        public ExperimentContext build(final String hash) {
            Optional.ofNullable(hash)
                    .orElseThrow(() -> new IllegalStateException("ExperimentContext cannot be build without hash"));
            return new ExperimentContextImpl(hash, this);
        }
    }
}
