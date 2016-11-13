package org.craftsmenlabs.gareth.core.context;

import lombok.Getter;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.core.invoker.MethodDescriptor;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Duration;
import java.util.Optional;

/**
 * Contains all data of an experiment definition, including references to the methods of the corresponding Definition class
 */
@XmlRootElement
@Getter
public class ExperimentContext {

    private final String hash;

    private final String experimentName;

    private final int weight;

    private final MethodDescriptor baseline, assume, success, failure;

    private final Duration time;

    private final String baselineGlueLine, assumeGlueLine, successGlueLine, failureGlueLine, timeGlueLine;

    private ExperimentContext(final String hash, final Builder builder) {
        this.hash = hash;
        this.experimentName = builder.experimentName;
        this.weight = builder.weight;
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
        public int weight;
        private MethodDescriptor baseline, assume, success, failure;
        private Duration time;

        public Builder(final String experimentName, final AssumptionBlock assumptionBlock) {
            this.experimentName = experimentName;
            this.assumptionBlock = assumptionBlock;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
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

        public ExperimentContext build(final String hash) {
            Optional.ofNullable(hash)
                    .orElseThrow(() -> new IllegalStateException("ExperimentContext cannot be build without hash"));
            return new ExperimentContext(hash, this);
        }
    }
}
