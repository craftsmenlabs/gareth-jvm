package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.application.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.application.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.application.definition.SaleofFruit;
import org.craftsmenlabs.gareth.application.definition.SampleDefinition;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.ExperimentEngineBuilder;
import org.craftsmenlabs.gareth.json.persist.JsonExperimentEnginePersistence;
import org.craftsmenlabs.gareth.rest.config.RestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@Import({CorsConfiguration.class, RestConfiguration.class})
@ComponentScan(basePackages = {"org.craftsmenlabs.gareth.application"})
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
    ExperimentEngine experimentEngine(ExperimentEngineConfig experimentEngineConfig){
        return new ExperimentEngineBuilder(experimentEngineConfig)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build())
                .build();
    }

}