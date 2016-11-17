package org.craftsmenlabs.gareth.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Config {
    @Bean
    open fun getObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper
    }
}