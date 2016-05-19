package org.craftsmenlabs.gareth.core;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.api.observer.Observer;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.api.rest.RestService;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.api.storage.StorageFactory;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExperimentEngineImpl implements ExperimentEngine {

    private final static Logger logger = LoggerFactory.getLogger(ExperimentEngineImpl.class);

    @Getter
    private final DefinitionRegistry definitionRegistry;

    private final ParsedDefinitionFactory parsedDefinitionFactory;

    private final ExperimentFactory experimentFactory;

    private final ExperimentRegistry experimentRegistry;

    private final ExperimentEngineConfig experimentEngineConfig;

    @Getter
    private final List<ExperimentContext> experimentContexts = new ArrayList<>();

    @Getter
    private final List<ExperimentRunContext> experimentRunContexts = new ArrayList<>();

    private final RestServiceFactory restServiceFactory;

    private final StorageFactory storageFactory;

    private final ExperimentEnginePersistence experimentEnginePersistence;

    private final Observer observer;

    private ExperimentContextBuilder experimentContextBuilder;

    private ExperimentRunner experimentRunner;

    @Getter
    private boolean started;


    ExperimentEngineImpl(final ExperimentEngineImplBuilder builder) {
        this.experimentEngineConfig = builder.experimentEngineConfig;
        this.definitionRegistry = builder.definitionRegistry;
        this.parsedDefinitionFactory = builder.parsedDefinitionFactory;
        this.experimentFactory = builder.experimentFactory;
        this.experimentRegistry = builder.experimentRegistry;
        this.restServiceFactory = builder.restServiceFactory;
        this.storageFactory = builder.storageFactory;
        this.experimentEnginePersistence = builder.experimentEnginePersistence;
        this.observer = builder.observer;
        experimentContextBuilder = new ExperimentContextBuilder(definitionRegistry, experimentEngineConfig);
        experimentRunner = new ExperimentRunner(builder.methodInvoker, builder.assumeScheduler, experimentEngineConfig
                .isIgnoreInvocationExceptions());

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


    private void init() {
        logger.info("Initializing experiment engine");
        initDefinitions();
        initExperiments();
        experimentContexts.addAll(experimentContextBuilder.build(experimentRegistry.getAllExperiments()));
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

    public String runExperiment(final Experiment experiment) {
        experimentRegistry.addExperiment(experiment.getExperimentName(), experiment);
        ExperimentContext context = experimentContextBuilder.build(experiment);
        final ExperimentRunContext experimentRunContext = new ExperimentRunContextImpl
                .Builder(context, storageFactory.createStorage())
                .build();
        planExperimentRunContext(experimentRunContext);
        return experimentRunContext.getHash();
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
            experimentRunner.invokeBaseline(experimentRunContext, () -> observer.notifyApplicationStateChanged(this));

            experimentRunner.scheduleInvokeAssume(experimentRunContext, this);
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

}
