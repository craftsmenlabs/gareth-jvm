package org.craftsmenlabs.gareth.core;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.invoker.MethodDescriptor;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.observer.Observer;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.api.rest.RestService;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.craftsmenlabs.gareth.api.storage.StorageFactory;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.craftsmenlabs.gareth.core.storage.DefaultStorageFactory;
import org.craftsmenlabs.gareth.core.util.ExperimentContextHashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Getter
    private final List<ExperimentContext> experimentContexts = new ArrayList<>();

    @Getter
    private final List<ExperimentRunContext> experimentRunContexts = new ArrayList<>();

    private final RestServiceFactory restServiceFactory;

    private final StorageFactory storageFactory;

    private final ExperimentEnginePersistence experimentEnginePersistence;

    private final Observer observer;

    @Getter
    private boolean started;


    private ExperimentEngineImpl(final Builder builder) {
        this.experimentEngineConfig = builder.experimentEngineConfig;
        this.definitionRegistry = builder.definitionRegistry;
        this.parsedDefinitionFactory = builder.parsedDefinitionFactory;
        this.experimentFactory = builder.experimentFactory;
        this.experimentRegistry = builder.experimentRegistry;
        this.methodInvoker = builder.methodInvoker;
        this.assumeScheduler = builder.assumeScheduler;
        this.restServiceFactory = builder.restServiceFactory;
        this.storageFactory = builder.storageFactory;
        this.experimentEnginePersistence = builder.experimentEnginePersistence;
        this.observer = builder.observer;
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
        if (isStarted()) {
            throw new IllegalStateException("Experiment engine already started");
        }
        started = true;
        logger.info("Starting experiment engine");
        init();
        startRestService();
        runExperiments();
    }

    @Override
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Experiment engine is not started");
        }
        observer.notifyApplicationStateChanged(this);
    }

    private void populateExperimentContexts() {
        logger.info("Populating experiment contexts");
        for (final Experiment experiment : experimentRegistry.getAllExperiments()) {
            for (final AssumptionBlock assumptionBlock : experiment.getAssumptionBlockList()) {

                final String[] surrogateKey = {experiment.getExperimentName()
                        , assumptionBlock.getBaseline()
                        , assumptionBlock.getAssumption()
                        , assumptionBlock.getTime()
                        , assumptionBlock.getSuccess()
                        , assumptionBlock.getFailure()};

                final String hashedSurrogateKey = ExperimentContextHashGenerator.generateHash(surrogateKey);

                final ExperimentContext experimentContext = new ExperimentContextImpl
                        .Builder(experiment.getExperimentName(), assumptionBlock)
                        .setBaseline(Optional.ofNullable(getBaseline(assumptionBlock.getBaseline())))
                        .setAssume(Optional.ofNullable(getAssume(assumptionBlock.getAssumption())))
                        .setTime(getDuration(assumptionBlock.getTime()))
                        .setFailure(Optional.ofNullable(getFailure(assumptionBlock.getFailure())))
                        .setSuccess(Optional.ofNullable(getSuccess(assumptionBlock.getSuccess())))
                        .build(hashedSurrogateKey);

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
        loadStateFromPersistence();
    }

    private void loadStateFromPersistence() {
        if (null != experimentEnginePersistence) {
            try {
                experimentEnginePersistence.restore(this);
            } catch (final GarethStateReadException e) {

            }
        }
    }

    private void startRestService() {
        if (null != restServiceFactory) {
            try {
                logger.info("Starting REST service");
                final RestService restService = restServiceFactory.create(this, 8888);
                restService.start();

            } catch (final Exception e) {
                logger.error("REST service cannot be started", e);
            }
        }
    }

    private void runExperiments() {
        logger.info("Run and schedule experiments");
        for (final ExperimentContext experimentContext : experimentContexts) {
            if (isNewExperimentRunContextNeeded(experimentContext.getHash())) {
                final ExperimentRunContext experimentRunContext = new ExperimentRunContextImpl
                        .Builder(experimentContext, storageFactory.createStorage())
                        .build();
                experimentRunContexts.add(experimentRunContext);
            }
        }
        getExperimentRunContexts().forEach(erc -> planExperimentRunContext(erc));
    }

    private boolean isNewExperimentRunContextNeeded(final String hash) {
        return getExperimentRunContexts()
                .stream()
                .filter(erc -> hash.equals(erc.getHash()))
                .count() == 0L;
    }

    @Override
    public void planExperimentContext(final ExperimentContext experimentContext) {
        final ExperimentRunContext experimentRunContext = new ExperimentRunContextImpl
                .Builder(experimentContext, storageFactory.createStorage())
                .build();
        experimentRunContexts.add(experimentRunContext);
        planExperimentRunContext(experimentRunContext);
    }

    public void planExperimentRunContext(final ExperimentRunContext experimentRunContext) {
        if (!isStarted()) throw new IllegalStateException("Cannot plan experiment context when engine is not started");
        if (experimentRunContext.getExperimentContext().isValid()) {
            invokeBaseline(experimentRunContext);
            scheduleInvokeAssume(experimentRunContext);
            experimentRunContext.setFinished(true);
        }
    }

    @Override
    public List<ExperimentRunContext> findExperimentRunContextsForHash(final String hash) {
        if (hash == null) throw new IllegalArgumentException("Hash cannot be null");

        return getExperimentRunContexts()
                .stream()
                .filter(erc -> hash.equals(erc.getHash()))
                .collect(Collectors.toList());
    }

    @Override
    public ExperimentContext findExperimentContextForHash(final String hash) throws GarethUnknownExperimentException {
        if (!isStarted()) throw new IllegalStateException("Cannot plan experiment context when engine is not started");
        if (hash == null) throw new IllegalArgumentException("Hash cannot be null");
        final Optional<ExperimentContext> experimentContext = experimentContexts
                .stream()
                .filter(ec -> ec.getHash().equals(hash))
                .findFirst();

        return experimentContext
                .orElseThrow(() -> new GarethUnknownExperimentException("Cannot find experiment context for hash"));
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

    private void invokeBaseline(final ExperimentRunContext runContext) {
        //final MethodDescriptor baselineMethodDescriptor = runContext.getExperimentContext().getBaseline();
        String glueLineInExperiment = runContext.getExperimentContext().getBaselineGlueLine();
        logger.debug(String.format("Invoking baseline: %s with state %s", glueLineInExperiment, runContext
                .getBaselineState().getName()));
        if (ExperimentPartState.OPEN == runContext.getBaselineState()) {
            try {
                runContext.setBaselineState(ExperimentPartState.RUNNING);
                MethodDescriptor baseline = runContext.getExperimentContext().getBaseline();
                if (runContext.getExperimentContext().hasStorage()) {
                    methodInvoker.invoke(glueLineInExperiment, baseline, runContext.getStorage());
                } else {
                    methodInvoker.invoke(glueLineInExperiment, baseline);
                }
                runContext.setBaselineState(ExperimentPartState.FINISHED);
                runContext.setBaselineRun(LocalDateTime.now());
            } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
                runContext.setBaselineState(ExperimentPartState.ERROR);
                if (!experimentEngineConfig.isIgnoreInvocationExceptions()) {
                    throw e;
                }
            }
            observer.notifyApplicationStateChanged(this);
        }
    }

    private void scheduleInvokeAssume(final ExperimentRunContext experimentRunContext) {
        if (ExperimentPartState.OPEN == experimentRunContext.getAssumeState()) {
            try {
                assumeScheduler.schedule(experimentRunContext, this);

            } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
                if (!experimentEngineConfig.isIgnoreInvocationExceptions()) {
                    throw e;
                }
            }
        }
    }

    private MethodDescriptor getSuccess(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForSuccess(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private MethodDescriptor getAssume(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForAssume(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }


    private MethodDescriptor getBaseline(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForBaseline(glueLine);
        } catch (final GarethUnknownDefinitionException e) {
            if (experimentEngineConfig.isIgnoreInvalidDefinitions()) {
                throw e;
            }
        }
        return method;
    }

    private MethodDescriptor getFailure(final String glueLine) {
        MethodDescriptor method = null;
        try {
            method = definitionRegistry.getMethodDescriptorForFailure(glueLine);
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
        parsedDefinition.getBaselineDefinitions()
                        .forEach((k, v) -> definitionRegistry.addMethodDescriptorForBaseline(k, v));
        parsedDefinition.getAssumeDefinitions()
                        .forEach((k, v) -> definitionRegistry.addMethodDescriptorForAssume(k, v));
        parsedDefinition.getFailureDefinitions()
                        .forEach((k, v) -> definitionRegistry.addMethodDescriptorForFailure(k, v));
        parsedDefinition.getSuccessDefinitions()
                        .forEach((k, v) -> definitionRegistry.addMethodDescriptorForSuccess(k, v));
        parsedDefinition.getTimeDefinitions().forEach((k, v) -> definitionRegistry.addDurationForTime(k, v));
    }

    /**
     * Experiment engine builder class
     */
    public static class Builder {

        private final ExperimentEngineConfig experimentEngineConfig;
        private DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();
        private ReflectionHelper reflectionHelper = new ReflectionHelper();
        private ParsedDefinitionFactory parsedDefinitionFactory = new ParsedDefinitionFactoryImpl(reflectionHelper);
        private MethodInvoker methodInvoker = new MethodInvokerImpl(reflectionHelper);
        private ExperimentFactory experimentFactory = new ExperimentFactoryImpl();
        private ExperimentRegistry experimentRegistry = new ExperimentRegistryImpl();
        private AssumeScheduler assumeScheduler = null;
        private RestServiceFactory restServiceFactory;
        private StorageFactory storageFactory = new DefaultStorageFactory();
        private Observer observer = new DefaultObserver();
        private ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
                .build();

        public Builder(final ExperimentEngineConfig experimentEngineConfig) {
            this.experimentEngineConfig = experimentEngineConfig;
        }

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

        public Builder setStorageFactory(final StorageFactory storageFactory) {
            this.storageFactory = storageFactory;
            return this;
        }

        public Builder setRestServiceFactory(final RestServiceFactory restServiceFactory) {
            this.restServiceFactory = restServiceFactory;
            return this;
        }

        public Builder setExperimentEnginePersistence(final ExperimentEnginePersistence experimentEnginePersistence) {
            this.experimentEnginePersistence = experimentEnginePersistence;
            return this;
        }

        public Builder setAssumeScheduler(final AssumeScheduler assumeScheduler) {
            this.assumeScheduler = assumeScheduler;
            return this;
        }

        private void buildDefaultAssumeScheduler() {
            if (assumeScheduler == null) {
                assumeScheduler = new DefaultAssumeScheduler
                        .Builder(observer)
                        .setIgnoreInvocationExceptions(experimentEngineConfig.isIgnoreInvocationExceptions())
                        .build();
            }
        }

        private void registerObservables() {
            if (null != experimentEnginePersistence) {
                observer.registerExperimentStateChangeListener(experimentEnginePersistence
                        .getExperimentStateChangeListener());
            }
        }

        public ExperimentEngine build() {
            registerObservables();
            buildDefaultAssumeScheduler();
            return new ExperimentEngineImpl(this);
        }
    }


}
