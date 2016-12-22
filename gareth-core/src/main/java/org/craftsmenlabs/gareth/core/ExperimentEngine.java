package org.craftsmenlabs.gareth.core;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.craftsmenlabs.gareth.api.exception.GarethDefinitionParseException;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.exception.GarethStateReadException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.Experiment;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.core.context.ExperimentRunContext;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.core.storage.DefaultStorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExperimentEngine {

    private final static Logger logger = LoggerFactory.getLogger(ExperimentEngine.class);

    private final ExperimentFactory experimentFactory;

    private final ExperimentRegistry experimentRegistry;

    private final ExperimentEngineConfig experimentEngineConfig;

    @Getter
    private final List<ExperimentContext> experimentContexts = new ArrayList<>();

    @Getter
    private final List<ExperimentRunContext> experimentRunContexts = new ArrayList<>();

    private final DefaultStorageFactory storageFactory;

    private final ExperimentEnginePersistence experimentEnginePersistence;

    private final DefaultObserver observer;

    private ExperimentContextBuilder experimentContextBuilder;

    private ExperimentRunner experimentRunner;

    @Getter
    private boolean started;


    protected ExperimentEngine(final ExperimentEngineBuilder builder) {
        this.experimentEngineConfig = builder.experimentEngineConfig;
        this.experimentFactory = builder.experimentFactory;
        this.experimentRegistry = builder.experimentRegistry;
        this.storageFactory = builder.storageFactory;
        this.experimentEnginePersistence = builder.experimentEnginePersistence;
        this.observer = builder.observer;
        experimentContextBuilder = new ExperimentContextBuilder(definitionRegistry, experimentEngineConfig);
        experimentRunner = new ExperimentRunner(builder.methodInvoker, builder.assumeScheduler, experimentEngineConfig
                .isIgnoreInvocationExceptions());

    }

    private void registerExperiment(final InputStream inputStream) {
        final Experiment experiment = experimentFactory.buildExperiment(inputStream);
        experimentRegistry.addExperiment(experiment.getExperimentName(), experiment);
    }

    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("Experiment engine already started");
        }
        started = true;
        logger.info("Starting experiment engine");
        init();
        runExperiments();
    }

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

    public String runExperiment(final Experiment experiment) {
        experimentRegistry.addExperiment(experiment.getExperimentName(), experiment);
        ExperimentContext context = experimentContextBuilder.build(experiment);
        experimentContexts.add(context);
        final ExperimentRunContext experimentRunContext = new ExperimentRunContext
                .Builder(context, storageFactory.createStorage())
                .build();
        experimentRunContexts.add(experimentRunContext);
        planExperimentRunContext(experimentRunContext);
        return experimentRunContext.getHash();
    }

    private void runExperiments() {
        logger.info("Run and schedule experiments");
        for (final ExperimentContext experimentContext : experimentContexts) {
            if (isNewExperimentRunContextNeeded(experimentContext.getHash())) {
                final ExperimentRunContext experimentRunContext = new ExperimentRunContext
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

    public void planExperimentContext(final ExperimentContext experimentContext) {
        final ExperimentRunContext experimentRunContext = new ExperimentRunContext
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

    public List<ExperimentRunContext> findExperimentRunContextsForHash(final String hash) {
        if (hash == null) throw new IllegalArgumentException("Hash cannot be null");

        return getExperimentRunContexts()
                .stream()
                .filter(erc -> hash.equals(erc.getHash()))
                .collect(Collectors.toList());
    }

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

}
