package org.craftsmenlabs.gareth2.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth2"))
open class CoreConfiguration {

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        return mapper
    }

}
