package org.craftsmenlabs.gareth.atdd

import org.craftsmenlabs.gareth.rest.RestClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@Import(RestClientConfig::class)
open class CucumberConfig {

    @Configuration
    @PropertySource("classpath:application.properties")
    open class PropertiesConfig {
        @Bean
        open fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
            return PropertySourcesPlaceholderConfigurer()
        }
    }
}
