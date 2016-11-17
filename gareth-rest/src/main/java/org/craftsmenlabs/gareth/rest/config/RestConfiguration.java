package org.craftsmenlabs.gareth.rest.config;

import org.craftsmenlabs.gareth.core.parser.GlueLineMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@Import({CorsConfiguration.class})
@ComponentScan(basePackages = {"org.craftsmenlabs.gareth.rest"})
public class RestConfiguration {

    @Bean
    public GlueLineMatcher glueLineMatcher() {
        return new GlueLineMatcher();
    }
}
