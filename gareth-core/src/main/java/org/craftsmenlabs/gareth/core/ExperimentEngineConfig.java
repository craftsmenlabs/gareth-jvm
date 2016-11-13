package org.craftsmenlabs.gareth.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExperimentEngineConfig {


    private final Set<Class> definitionClasses;

    private final List<InputStream> inputStreamList;

    private final boolean ignoreInvalidDefinitions;
    private final boolean ignoreInvalidExperiments;
    private final boolean ignoreInvocationExceptions;

    private ExperimentEngineConfig(final Builder builder) {
        this.definitionClasses = builder.definitionClasses;
        this.inputStreamList = builder.inputStreams;
        this.ignoreInvalidDefinitions = builder.ignoreInvalidDefinitions;
        this.ignoreInvalidExperiments = builder.ignoreInvalidExperiments;
        this.ignoreInvocationExceptions = builder.ignoreInvocationExceptions;
    }

    public Class[] getDefinitionClasses() {
        return definitionClasses.toArray(new Class[definitionClasses.size()]);
    }

    public InputStream[] getInputStreams() {
        return this.inputStreamList.toArray(new InputStream[this.inputStreamList.size()]);
    }

    public boolean isIgnoreInvalidDefinitions() {
        return ignoreInvalidDefinitions;
    }

    public boolean isIgnoreInvalidExperiments() {
        return ignoreInvalidExperiments;
    }

    public boolean isIgnoreInvocationExceptions() {
        return ignoreInvocationExceptions;
    }

    /**
     * Experiment engine config builder
     */
    public static class Builder {

        private final Set<Class> definitionClasses = new HashSet<>();
        private final List<InputStream> inputStreams = new ArrayList<>();
        private boolean ignoreInvalidDefinitions, ignoreInvalidExperiments, ignoreInvocationExceptions;

        public Builder addInputStreams(final InputStream inputStream) {
            this.inputStreams.add(inputStream);
            return this;
        }

        public Builder addInputStreams(final Collection<InputStream> inputStreams) {
            this.inputStreams.addAll(inputStreams);
            return this;
        }

        public Builder addDefinitionClass(final Class definitionClass) {
            definitionClasses.add(definitionClass);
            return this;
        }

        public Builder addDefinitionClasses(final Collection<Class> definitionClasses) {
            this.definitionClasses.addAll(definitionClasses);
            return this;
        }

        public Builder setIgnoreInvalidDefinitions(boolean ignoreInvalidDefinitions) {
            this.ignoreInvalidDefinitions = ignoreInvalidDefinitions;
            return this;
        }

        public Builder setIgnoreInvalidExperiments(boolean ignoreInvalidExperiments) {
            this.ignoreInvalidExperiments = ignoreInvalidExperiments;
            return this;
        }

        public Builder setIgnoreInvocationExceptions(boolean ignoreInvocationExceptions) {
            this.ignoreInvocationExceptions = ignoreInvocationExceptions;
            return this;
        }

        public ExperimentEngineConfig build() {
            return new ExperimentEngineConfig(this);
        }
    }
}
