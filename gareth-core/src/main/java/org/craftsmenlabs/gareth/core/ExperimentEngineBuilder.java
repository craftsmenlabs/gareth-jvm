package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.core.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.core.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.reflection.DefinitionFactory;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.craftsmenlabs.gareth.core.storage.DefaultStorageFactory;

public class ExperimentEngineBuilder {

    protected final ExperimentEngineConfig experimentEngineConfig;
    protected DefinitionRegistry definitionRegistry = new DefinitionRegistry();
    protected DefinitionFactory customDefinitionFactory;

    protected ReflectionHelper reflectionHelper;

    protected ParsedDefinitionFactory parsedDefinitionFactory;

    protected MethodInvoker methodInvoker;

    protected ExperimentFactory experimentFactory = new ExperimentFactory();
    protected ExperimentRegistry experimentRegistry = new ExperimentRegistry();
    protected DefaultAssumeScheduler assumeScheduler = null;
    protected DefaultStorageFactory storageFactory = new DefaultStorageFactory();
    protected DefaultObserver observer = new DefaultObserver();
    protected ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
            .build();

    public ExperimentEngineBuilder(final ExperimentEngineConfig experimentEngineConfig) {
        this.experimentEngineConfig = experimentEngineConfig;
    }

    public ExperimentEngineBuilder setDefinitionRegistry(final DefinitionRegistry definitionRegistry) {
        this.definitionRegistry = definitionRegistry;
        return this;
    }

    public ExperimentEngineBuilder setParsedDefinitionFactory(final ParsedDefinitionFactory parsedDefinitionFactory) {
        this.parsedDefinitionFactory = parsedDefinitionFactory;
        return this;
    }

    public ExperimentEngineBuilder setExperimentRegistry(final ExperimentRegistry experimentRegistry) {
        this.experimentRegistry = experimentRegistry;
        return this;
    }

    public ExperimentEngineBuilder setMethodInvoker(final MethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
        return this;
    }


    public ExperimentEngineBuilder setExperimentFactory(final ExperimentFactory experimentFactory) {
        this.experimentFactory = experimentFactory;
        return this;
    }

    public ExperimentEngineBuilder setStorageFactory(final DefaultStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        return this;
    }

    public ExperimentEngineBuilder setExperimentEnginePersistence(final ExperimentEnginePersistence experimentEnginePersistence) {
        this.experimentEnginePersistence = experimentEnginePersistence;
        return this;
    }

    public ExperimentEngineBuilder setAssumeScheduler(final DefaultAssumeScheduler assumeScheduler) {
        this.assumeScheduler = assumeScheduler;
        return this;
    }

    public ExperimentEngineBuilder addCustomDefinitionFactory(DefinitionFactory definitionFactory) {
        this.customDefinitionFactory = definitionFactory;
        return this;
    }

    public ExperimentEngine build() {
        reflectionHelper = new ReflectionHelper(customDefinitionFactory);

        builParsedDefinitionFactory();
        builMethodInvoker();
        registerObservables();
        buildDefaultAssumeScheduler();

        return new ExperimentEngine(this);
    }

    private void builParsedDefinitionFactory() {
        if (parsedDefinitionFactory == null) {
            parsedDefinitionFactory = new ParsedDefinitionFactory(reflectionHelper);
        }
    }

    private void builMethodInvoker() {
        if (methodInvoker == null) {
            methodInvoker = new MethodInvoker(reflectionHelper);
        }
    }

    private void buildDefaultAssumeScheduler() {
        if (assumeScheduler == null) {
            assumeScheduler = new DefaultAssumeScheduler
                    .Builder(observer)
                    .addCustomDefinitionFactory(customDefinitionFactory)
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
}
