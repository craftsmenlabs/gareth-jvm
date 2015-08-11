package org.craftsmenlabs.gareth.core;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinition;
import org.craftsmenlabs.gareth.api.definition.ParsedDefinitionFactory;
import org.craftsmenlabs.gareth.api.exception.GarethExperimentParseException;
import org.craftsmenlabs.gareth.api.registry.DefinitionRegistry;
import org.craftsmenlabs.gareth.core.parser.ParsedDefinitionFactoryImpl;
import org.craftsmenlabs.gareth.core.registry.DefinitionRegistryImpl;

import java.io.InputStream;

/**
 * Created by hylke on 10/08/15.
 */
public class ExperimentEngineImpl implements ExperimentEngine {

    private final DefinitionRegistry definitionRegistry;

    private final ParsedDefinitionFactory parsedDefinitionFactory;

    private ExperimentEngineImpl(final Builder builder) {
        this.definitionRegistry = builder.definitionRegistry;
        this.parsedDefinitionFactory = builder.parsedDefinitionFactory;
    }

    @Override
    public void registerDefinition(final Class clazz) throws GarethExperimentParseException {
        final ParsedDefinition parsedDefinition = parsedDefinitionFactory.parse(clazz);
    }

    @Override
    public void registerExperiment(final InputStream inputStream) {

    }

    public static class Builder {

        private DefinitionRegistry definitionRegistry = new DefinitionRegistryImpl();

        private ParsedDefinitionFactory parsedDefinitionFactory = new ParsedDefinitionFactoryImpl();

        public void setDefinitionRegistry(final DefinitionRegistry definitionRegistry) {
            this.definitionRegistry = definitionRegistry;
        }

        public void setParsedDefinitionFactory(final ParsedDefinitionFactory parsedDefinitionFactory) {
            this.parsedDefinitionFactory = parsedDefinitionFactory;
        }

        public ExperimentEngine build() {
            return new ExperimentEngineImpl(this);
        }
    }

    private void addParsedDefinitionInRegistery(final ParsedDefinition parsedDefinition){

    }


}
