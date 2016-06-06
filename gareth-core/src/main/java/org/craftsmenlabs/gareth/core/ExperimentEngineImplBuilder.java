package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.factory.ExperimentFactory;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.observer.Observer;
import org.craftsmenlabs.gareth.api.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.api.registry.ExperimentRegistry;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.craftsmenlabs.gareth.api.storage.StorageFactory;
import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.reflection.DefinitionFactory;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.craftsmenlabs.gareth.core.storage.DefaultStorageFactory;

public class ExperimentEngineImplBuilder {

    protected final ExperimentEngineConfig experimentEngineConfig;
    protected DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();
    protected DefinitionFactory customDefinitionFactory;

    protected ReflectionHelper reflectionHelper;

    protected ParsedDefinitionFactory parsedDefinitionFactory;

    protected MethodInvoker methodInvoker;

    protected ExperimentFactory experimentFactory = new ExperimentFactoryImpl();
    protected ExperimentRegistry experimentRegistry = new ExperimentRegistryImpl();
    protected AssumeScheduler assumeScheduler = null;
    protected RestServiceFactory restServiceFactory;
    protected StorageFactory storageFactory = new DefaultStorageFactory();
    protected Observer observer = new DefaultObserver();
    protected ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
            .build();

    public ExperimentEngineImplBuilder(final ExperimentEngineConfig experimentEngineConfig) {
        this.experimentEngineConfig = experimentEngineConfig;
    }

    public ExperimentEngineImplBuilder setDefinitionRegistry(final DefinitionRegistry definitionRegistry) {
        this.definitionRegistry = definitionRegistry;
        return this;
    }

    public ExperimentEngineImplBuilder setParsedDefinitionFactory(final ParsedDefinitionFactory parsedDefinitionFactory) {
        this.parsedDefinitionFactory = parsedDefinitionFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setExperimentRegistry(final ExperimentRegistry experimentRegistry) {
        this.experimentRegistry = experimentRegistry;
        return this;
    }

    public ExperimentEngineImplBuilder setMethodInvoker(final MethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
        return this;
    }


    public ExperimentEngineImplBuilder setExperimentFactory(final ExperimentFactory experimentFactory) {
        this.experimentFactory = experimentFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setStorageFactory(final StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setRestServiceFactory(final RestServiceFactory restServiceFactory) {
        this.restServiceFactory = restServiceFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setExperimentEnginePersistence(final ExperimentEnginePersistence experimentEnginePersistence) {
        this.experimentEnginePersistence = experimentEnginePersistence;
        return this;
    }

    public ExperimentEngineImplBuilder setAssumeScheduler(final AssumeScheduler assumeScheduler) {
        this.assumeScheduler = assumeScheduler;
        return this;
    }

    public ExperimentEngineImplBuilder addCustomDefinitionFactory(DefinitionFactory definitionFactory) {
        this.customDefinitionFactory = definitionFactory;
        return this;
    }

    public ExperimentEngine build() {
        reflectionHelper = new ReflectionHelper(customDefinitionFactory);

        builParsedDefinitionFactory();
        builMethodInvoker();
        registerObservables();
        buildDefaultAssumeScheduler();

        return new ExperimentEngineImpl(this);
    }

    private void builParsedDefinitionFactory() {
        if (parsedDefinitionFactory == null) {
            parsedDefinitionFactory = new ParsedDefinitionFactoryImpl(reflectionHelper);
        }
    }

    private void builMethodInvoker() {
        if (methodInvoker == null) {
            methodInvoker = new MethodInvokerImpl(reflectionHelper);
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
