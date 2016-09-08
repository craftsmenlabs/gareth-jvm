package org.craftsmenlabs.gareth.core.context;

import lombok.Getter;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Duration;
import java.util.Optional;

@XmlRootElement
@Getter
public class ExperimentContextImpl {

    private final String hash;

    private final String experimentName;

    private final MethodDescriptor baseline, assume, success, failure;

    private final Duration time;

    private final String baselineGlueLine, assumeGlueLine, successGlueLine, failureGlueLine, timeGlueLine;

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

        // Time
        this.time = builder.time;

    }


    public boolean isValid() {
        return null != baseline
                && null != assume
                && null != time;
    }


    public boolean hasStorage() {
        return getAssume().hasStorage()
                || getBaseline().hasStorage()
                || (null != getFailure() && getFailure().hasStorage())
                || (null != getSuccess() && getSuccess().hasStorage());
    }

    public static class Builder {

        private final String experimentName;

        private final AssumptionBlock assumptionBlock;

        private MethodDescriptor baseline, assume, success, failure;

        private Duration time;

        public Builder(final String experimentName, final AssumptionBlock assumptionBlock) {
            this.experimentName = experimentName;
            this.assumptionBlock = assumptionBlock;
        }

        public Builder setBaseline(final Optional<MethodDescriptor> baseline) {
            if (baseline.isPresent()) {
                this.baseline = baseline.get();
            }
            return this;
        }

        public Builder setAssume(final Optional<MethodDescriptor> assume) {
            if (assume.isPresent()) {
                this.assume = assume.get();
            }
            return this;
        }

        public Builder setSuccess(final Optional<MethodDescriptor> success) {
            if (success.isPresent()) {
                this.success = success.get();
            }
            return this;
        }

        public Builder setFailure(final Optional<MethodDescriptor> failure) {
            if (failure.isPresent()) {
                this.failure = failure.get();
            }
            return this;
        }

        public Builder setTime(final Duration time) {
            this.time = time;
            return this;
        }

        public ExperimentContextImpl build(final String hash) {
            Optional.ofNullable(hash)
                    .orElseThrow(() -> new IllegalStateException("ExperimentContext cannot be build without hash"));
            return new ExperimentContextImpl(hash, this);
        }
    }
}
