package org.craftsmenlabs.gareth.application;

import org.craftsmenlabs.gareth.application.definition.AnotherDefinition;
import org.craftsmenlabs.gareth.application.definition.RestDefinitionFactory;
import org.craftsmenlabs.gareth.application.definition.SaleofFruit;
import org.craftsmenlabs.gareth.application.definition.SampleDefinition;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImplBuilder;
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
    ExperimentEngineConfigImpl experimentEngineConfigImpl() {
        return new ExperimentEngineConfigImpl
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
    ExperimentEngineImpl experimentEngine(ExperimentEngineConfigImpl experimentEngineConfigImpl){
        return new ExperimentEngineImplBuilder(experimentEngineConfigImpl)
                .addCustomDefinitionFactory(new RestDefinitionFactory())
                .setExperimentEnginePersistence(new JsonExperimentEnginePersistence.Builder().build())
                .build();
    }

}
