package org.craftsmenlabs.gareth.core.context;

import lombok.Getter;
import lombok.Setter;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by hylke on 10/08/15.
 */
@Getter
public class ExperimentContextImpl implements ExperimentContext {

    private final String experimentName;

    private Method baseline, assume, success, failure;

    private Duration time;

    private String baselineGlueLine, assumeGlueLine, successGlueLine, failureGlueLine, timeGlueLine;

    private boolean finished;

    @Setter
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;

    private ExperimentContextImpl(final Builder builder) {
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
        // Time
        this.time = builder.time;

    }


    @Override
    public boolean isValid() {
        return null != baseline
                && null != assume
                && null != time;
    }

    @Override
    public boolean hasFailures() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public static class Builder {

        private final String experimentName;

        private final AssumptionBlock assumptionBlock;

        private Method baseline, assume, success, failure;

        private Duration time;

        public Builder(final String experimentName, final AssumptionBlock assumptionBlock) {
            this.experimentName = experimentName;
            this.assumptionBlock = assumptionBlock;
        }

        public Builder setBaseline(final Method baseline) {
            this.baseline = baseline;
            return this;
        }

        public Builder setAssume(final Method assume) {
            this.assume = assume;
            return this;
        }

        public Builder setSuccess(final Method success) {
            this.success = success;
            return this;
        }

        public Builder setFailure(final Method failure) {
            this.failure = failure;
            return this;
        }

        public Builder setTime(final Duration time) {
            this.time = time;
            return this;
        }

        public ExperimentContext build() {
            return new ExperimentContextImpl(this);
        }
    }
}
