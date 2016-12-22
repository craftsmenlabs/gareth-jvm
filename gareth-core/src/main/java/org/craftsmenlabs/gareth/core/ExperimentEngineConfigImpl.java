package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;

import java.io.InputStream;
import java.util.*;

public class ExperimentEngineConfigImpl implements ExperimentEngineConfig {


    private final Set<Class> definitionClasses;

    private final Set<String> definitionPackages;

    private final List<InputStream> inputStreamList;

    private final boolean ignoreInvalidDefinitions, ignoreInvalidExperiments, ignoreInvocationExceptions;

    private ExperimentEngineConfigImpl(final Builder builder) {
        this.definitionClasses = builder.definitionClasses;
        this.definitionPackages = builder.definitionPackages;
        this.inputStreamList = builder.inputStreams;
        this.ignoreInvalidDefinitions = builder.ignoreInvalidDefinitions;
        this.ignoreInvalidExperiments = builder.ignoreInvalidExperiments;
        this.ignoreInvocationExceptions = builder.ignoreInvocationExceptions;
    }

    @Override
    public Class[] getDefinitionClasses() {
        return definitionClasses.toArray(new Class[definitionClasses.size()]);
    }


    @Override
    public String[] getDefinitionPackages() {
        return definitionPackages.toArray(new String[definitionPackages.size()]);
    }

    @Override
    public InputStream[] getInputStreams() {
        return this.inputStreamList.toArray(new InputStream[this.inputStreamList.size()]);
    }

    @Override
    public boolean isIgnoreInvalidDefinitions() {
        return ignoreInvalidDefinitions;
    }

    @Override
    public boolean isIgnoreInvalidExperiments() {
        return ignoreInvalidExperiments;
    }

    @Override
    public boolean isIgnoreInvocationExceptions() {
        return ignoreInvocationExceptions;
    }

    /**
     * Experiment engine config builder
     */
    public static class Builder {

        private final Set<Class> definitionClasses = new HashSet<>();
        private final Set<String> definitionPackages = new HashSet<>();
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

        public Builder addDefinitionPackage(final String definitionPackage) {
            this.definitionPackages.add(definitionPackage);
            return this;
        }

        public Builder addDefinitionPackages(final Collection<String> definitionPackages) {
            this.definitionPackages.addAll(definitionPackages);
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
            return new ExperimentEngineConfigImpl(this);
        }
    }


}
