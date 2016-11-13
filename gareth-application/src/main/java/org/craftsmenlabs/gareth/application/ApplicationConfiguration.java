package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.application.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.application.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.application.definition.SaleofFruit;
import org.craftsmenlabs.gareth.application.definition.SampleDefinition;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.ExperimentEngineBuilder;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ExperimentEngineConfig experimentEngineConfigImpl() {
        return new ExperimentEngineConfig
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addDefinitionClass(AnotherDefinition.class)
                .addDefinitionClass(SaleofFruit.class)
                .addInputStreams(this.getClass()
                                     .getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(true)
                .build();
    }

    @Bean
    ExperimentEngine experimentEngine(ExperimentEngineConfig experimentEngineConfig) {
        return new ExperimentEngineBuilder(experimentEngineConfig)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence())
                .build();
    }
}
