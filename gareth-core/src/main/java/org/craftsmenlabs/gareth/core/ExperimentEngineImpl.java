package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;

import java.io.InputStream;

/**
 * Created by hylke on 10/08/15.
 */
public class ExperimentEngineImpl implements ExperimentEngine {

    private DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();

    private ExperimentEngineImpl(final Builder builder) {
        this.definitionRegistry = builder.definitionRegistry;
    }

    @Override
    public void loadDefinition(final Class clazz) {

    }

    @Override
    public void loadExperiment(final InputStream inputStream) {

    }

    public static class Builder {

        private DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();

        public void setDefinitionRegistry(final DefinitionRegistry definitionRegistry) {
            this.definitionRegistry = definitionRegistry;
        }

        public ExperimentEngine build() {
            return new ExperimentEngineImpl(this);
        }
    }


}
