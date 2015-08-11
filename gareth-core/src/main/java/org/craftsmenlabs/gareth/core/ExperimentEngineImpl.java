package org.craftsmenlabs.gareth.core;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;

import java.io.InputStream;

/**
 * Created by hylke on 10/08/15.
 */
public class ExperimentEngineImpl implements ExperimentEngine {

    private final DefinitionRegistry definitionRegistry;

    private final ParsedDefinitionFactory parsedDefinitionFactory;

    private final ExperimentFactory experimentFactory;

    private final ExperimentRegistry experimentRegistry;

    private final ExperimentEngineConfig experimentEngineConfig;

    private ExperimentEngineImpl(final Builder builder) {
        this.experimentEngineConfig = builder.experimentEngineConfig;
        this.definitionRegistry = builder.definitionRegistry;
        this.parsedDefinitionFactory = builder.parsedDefinitionFactory;
        this.experimentFactory = builder.experimentFactory;
        this.experimentRegistry = builder.experimentRegistry;
    }

    private void registerDefinition(final Class clazz) throws GarethDefinitionParseException {
        final ParsedDefinition parsedDefinition = parsedDefinitionFactory.parse(clazz);
        addParsedDefinitionToRegistry(parsedDefinition);
    }

    private void registerExperiment(final InputStream inputStream) {
        final Experiment experiment = experimentFactory.buildExperiment(inputStream);
        experimentRegistry.addExperiment(experiment.getExperimentName(), experiment);
    }

    @Override
    public void start() {
        init();
    }

    private void init() {
        initDefinitions();
        initExperiments();
    }

    private void initExperiments() throws GarethExperimentParseException {
        for (final InputStream inputStream : experimentEngineConfig.getInputStreams()) {
            try {
                registerExperiment(inputStream);
            } catch (GarethExperimentParseException e) {
                if (!experimentEngineConfig.isIgnoreInvalidExperiments()) {
                    throw e;
                }
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void initDefinitions() {
        for (final Class clazz : experimentEngineConfig.getDefinitionClasses()) {
            try {
                registerDefinition(clazz);
            } catch (final GarethDefinitionParseException e) {
                if (!experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                    throw e;
                }
            }
        }
    }

    private void addParsedDefinitionToRegistry(final ParsedDefinition parsedDefinition) {
        parsedDefinition.getBaselineDefinitions().forEach((k, v) -> definitionRegistry.addMethodForBaseline(k, v));
        parsedDefinition.getAssumeDefinitions().forEach((k, v) -> definitionRegistry.addMethodForAssume(k, v));
        parsedDefinition.getFailureDefinitions().forEach((k, v) -> definitionRegistry.addMethodForFailure(k, v));
        parsedDefinition.getSuccessDefinitions().forEach((k, v) -> definitionRegistry.addMethodForSuccess(k, v));
        parsedDefinition.getTimeDefinitions().forEach((k, v) -> definitionRegistry.addDurationForTime(k, v));
    }

    /**
     * Experiment engine builder class
     */
    public static class Builder {

        private final ExperimentEngineConfig experimentEngineConfig;

        public Builder(final ExperimentEngineConfig experimentEngineConfig) {
            this.experimentEngineConfig = experimentEngineConfig;
        }

        private DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();

        private ParsedDefinitionFactory parsedDefinitionFactory = new ParsedDefinitionFactoryImpl();

        private ExperimentFactory experimentFactory = new ExperimentFactoryImpl();

        private ExperimentRegistry experimentRegistry = new ExperimentRegistryImpl();

        public Builder setDefinitionRegistry(final DefinitionRegistry definitionRegistry) {
            this.definitionRegistry = definitionRegistry;
            return this;
        }

        public Builder setParsedDefinitionFactory(final ParsedDefinitionFactory parsedDefinitionFactory) {
            this.parsedDefinitionFactory = parsedDefinitionFactory;
            return this;
        }

        public Builder setExperimentRegistry(final ExperimentRegistry experimentRegistry) {
            this.experimentRegistry = experimentRegistry;
            return this;
        }

        public Builder setExperimentFactory(final ExperimentFactory experimentFactory) {
            this.experimentFactory = experimentFactory;
            return this;
        }

        public ExperimentEngine build() {
            return new ExperimentEngineImpl(this);
        }
    }


}
