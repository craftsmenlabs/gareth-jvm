package org.craftsmenlabs.gareth.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.execution"))
open class GarethExecutionConfig {
    @Bean
    open fun getObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper
    }
}