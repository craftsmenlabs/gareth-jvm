package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hylke on 11/08/15.
 */
public class ExperimentEngineConfigImpl implements ExperimentEngineConfig {


    private final Set<Class> definitionClasses;

    private final List<InputStream> inputStreamList;

    private final boolean ignoreInvalidDefinitions, ignoreInvalidExperiments;

    private ExperimentEngineConfigImpl(final Builder builder) {
        this.definitionClasses = builder.definitionClasses;
        this.inputStreamList = builder.inputStreams;
        this.ignoreInvalidDefinitions = builder.ignoreInvalidDefinitions;
        this.ignoreInvalidExperiments = builder.ignoreInvalidExperiments;
    }

    @Override
    public Class[] getDefinitionClasses() {
        return definitionClasses.toArray(new Class[definitionClasses.size()]);
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

    /**
     * Experiment engine config builder
     */
    public static class Builder {

        private boolean ignoreInvalidDefinitions, ignoreInvalidExperiments;

        private final Set<Class> definitionClasses = new HashSet<>();

        private final List<InputStream> inputStreams = new ArrayList<>();

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

        public ExperimentEngineConfig build() {
            return new ExperimentEngineConfigImpl(this);
        }
    }


}
