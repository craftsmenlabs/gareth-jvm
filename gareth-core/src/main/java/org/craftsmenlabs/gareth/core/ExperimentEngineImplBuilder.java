package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.core.factory.ExperimentFactoryImpl;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.observer.DefaultObserver;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.persist.ExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.persist.FileSystemExperimentEnginePersistence;
import org.craftsmenlabs.gareth.core.reflection.DefinitionFactory;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;
import org.craftsmenlabs.gareth.core.registry.ExperimentRegistryImpl;
import org.craftsmenlabs.gareth.core.scheduler.DefaultAssumeScheduler;
import org.craftsmenlabs.gareth.core.storage.DefaultStorageFactory;

public class ExperimentEngineImplBuilder {

    protected final ExperimentEngineConfigImpl experimentEngineConfig;
    protected DefinitionRegistryImpl definitionRegistry = new DefinitionRegistryImpl();
    protected DefinitionFactory customDefinitionFactory;

    protected ReflectionHelper reflectionHelper;

    protected ParsedDefinitionFactoryImpl parsedDefinitionFactory;

    protected MethodInvokerImpl methodInvoker;

    protected ExperimentFactoryImpl experimentFactory = new ExperimentFactoryImpl();
    protected ExperimentRegistryImpl experimentRegistry = new ExperimentRegistryImpl();
    protected DefaultAssumeScheduler assumeScheduler = null;
    protected DefaultStorageFactory storageFactory = new DefaultStorageFactory();
    protected DefaultObserver observer = new DefaultObserver();
    protected ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder()
            .build();

    public ExperimentEngineImplBuilder(final ExperimentEngineConfigImpl experimentEngineConfig) {
        this.experimentEngineConfig = experimentEngineConfig;
    }

    public ExperimentEngineImplBuilder setDefinitionRegistry(final DefinitionRegistryImpl definitionRegistry) {
        this.definitionRegistry = definitionRegistry;
        return this;
    }

    public ExperimentEngineImplBuilder setParsedDefinitionFactory(final ParsedDefinitionFactoryImpl parsedDefinitionFactory) {
        this.parsedDefinitionFactory = parsedDefinitionFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setExperimentRegistry(final ExperimentRegistryImpl experimentRegistry) {
        this.experimentRegistry = experimentRegistry;
        return this;
    }

    public ExperimentEngineImplBuilder setMethodInvoker(final MethodInvokerImpl methodInvoker) {
        this.methodInvoker = methodInvoker;
        return this;
    }


    public ExperimentEngineImplBuilder setExperimentFactory(final ExperimentFactoryImpl experimentFactory) {
        this.experimentFactory = experimentFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setStorageFactory(final DefaultStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
        return this;
    }

    public ExperimentEngineImplBuilder setExperimentEnginePersistence(final ExperimentEnginePersistence experimentEnginePersistence) {
        this.experimentEnginePersistence = experimentEnginePersistence;
        return this;
    }

    public ExperimentEngineImplBuilder setAssumeScheduler(final DefaultAssumeScheduler assumeScheduler) {
        this.assumeScheduler = assumeScheduler;
        return this;
    }

    public ExperimentEngineImplBuilder addCustomDefinitionFactory(DefinitionFactory definitionFactory) {
        this.customDefinitionFactory = definitionFactory;
        return this;
    }

    public ExperimentEngineImpl build() {
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
