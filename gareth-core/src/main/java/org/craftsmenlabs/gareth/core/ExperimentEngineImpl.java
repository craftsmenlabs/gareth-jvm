package org.craftsmenlabs.gareth.core;

import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hylke on 10/08/15.
 */
public class ExperimentEngineImpl implements ExperimentEngine {

    private final static Logger logger = LoggerFactory.getLogger(ExperimentEngineImpl.class);

    private final DefinitionRegistry definitionRegistry;

    private final ParsedDefinitionFactory parsedDefinitionFactory;

    private final ExperimentFactory experimentFactory;

    private final ExperimentRegistry experimentRegistry;

    private final ExperimentEngineConfig experimentEngineConfig;

    private final MethodInvoker methodInvoker;

    private final AssumeScheduler assumeScheduler;

    private final List<ExperimentContext> experimentContexts = new ArrayList<>();


    private ExperimentEngineImpl(final Builder builder) {
        this.experimentEngineConfig = builder.experimentEngineConfig;
        this.definitionRegistry = builder.definitionRegistry;
        this.parsedDefinitionFactory = builder.parsedDefinitionFactory;
        this.experimentFactory = builder.experimentFactory;
        this.experimentRegistry = builder.experimentRegistry;
        this.methodInvoker = builder.methodInvoker;
        this.assumeScheduler = builder.assumeScheduler;
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
        logger.info("Starting experiment engine");
        init();
        runExperiments();
    }

    private void populateExperimentContexts() {
        logger.info("Populating experiment contexts");
        for (final Experiment experiment : experimentRegistry.getAllExperiments()) {
            for (final AssumptionBlock assumptionBlock : experiment.getAssumptionBlockList()) {

                final ExperimentContext experimentContext = new ExperimentContextImpl
                        .Builder(experiment.getExperimentName(), assumptionBlock)
                        .setBaseline(getBaseline(assumptionBlock.getBaseline()))
                        .setAssume(getAssume(assumptionBlock.getAssumption()))
                        .setTime(getDuration(assumptionBlock.getTime()))
                        .setFailure(getFailure(assumptionBlock.getFailure()))
                        .setSuccess(getSuccess(assumptionBlock.getSuccess()))
                        .build();

                experimentContexts.add(experimentContext);
            }
        }
        logger.info(String.format("Added %d different experiments", experimentContexts.size()));
    }

    private void init() {
        logger.info("Initializing experiment engine");
        initDefinitions();
        initExperiments();
        populateExperimentContexts();

    }

    private void runExperiments() {
        logger.info("Run and schedule experiments");
        for (final ExperimentContext experimentContext : experimentContexts) {
            if (experimentContext.isValid()) {
                invokeBaseline(experimentContext.getBaseline());
                experimentContext.setBaselineRun(LocalDateTime.now());
                scheduleInvokeAssume(experimentContext);
                experimentContext.setFinished(true);
            }
        }
    }

    private Duration getDuration(final String timeGlueLine) {
        Duration duration = null;
        try {
            duration = definitionRegistry.getDurationForTime(timeGlueLine);
        } catch (final GarethUnknownDefinitionException e) {
            throw new GarethInvocationException(e);
        }
        return duration;
    }

    private void invokeBaseline(final Method baselineMethod) {
        try {
            methodInvoker.invoke(baselineMethod);
        } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
            if (!experimentEngineConfig.isIgnoreInvocationExceptions()) {
                throw e;
            }
        }
    }

    private void scheduleInvokeAssume(final ExperimentContext experimentContext) {
        try {
            assumeScheduler.schedule(experimentContext);

        } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
            if (!experimentEngineConfig.isIgnoreInvocationExceptions()) {
                throw e;
            }
        }

    }

    private Method getSuccess(final String glueLine) {
        Method method = null;
        try {
            method = definitionRegistry.getMethodForSuccess(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private Method getAssume(final String glueLine) {
        Method method = null;
        try {
            method = definitionRegistry.getMethodForAssume(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }


    private Method getBaseline(final String glueLine) {
        Method method = null;
        try {
            method = definitionRegistry.getMethodForBaseline(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private Method getFailure(final String glueLine) {
        Method method = null;
        try {
            method = definitionRegistry.getMethodForFailure(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
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


    @Override
    public List<ExperimentContext> getExperimentContexts() {
        return experimentContexts;
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

        private ReflectionHelper reflectionHelper = new ReflectionHelper();

        private ParsedDefinitionFactory parsedDefinitionFactory = new ParsedDefinitionFactoryImpl(reflectionHelper);

        private MethodInvoker methodInvoker = new MethodInvokerImpl(reflectionHelper);

        private ExperimentFactory experimentFactory = new ExperimentFactoryImpl();

        private ExperimentRegistry experimentRegistry = new ExperimentRegistryImpl();

        private AssumeScheduler assumeScheduler = null;

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

        public Builder setMethodInvoker(final MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
            return this;
        }


        public Builder setExperimentFactory(final ExperimentFactory experimentFactory) {
            this.experimentFactory = experimentFactory;
            return this;
        }

        private void buildDefaultAssumeScheduler() {
            if (assumeScheduler == null) {
                assumeScheduler = new DefaultAssumeScheduler
                        .Builder()
                        .setIgnoreInvocationExceptions(experimentEngineConfig.isIgnoreInvocationExceptions())
                        .build();
            }
        }

        public ExperimentEngine build() {
            buildDefaultAssumeScheduler();
            return new ExperimentEngineImpl(this);
        }
    }


}
