package org.craftsmenlabs.gareth.atdd

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@Configuration
open class CucumberConfig {

    @Autowired
    lateinit var context: ApplicationContext

    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
      //  GarethServerEnvironment.addInstance(GarethServerEnvironment.createGarethInstance())
      //  GarethServerEnvironment.addInstance(GarethServerEnvironment.createExecutionInstance())
       // GarethServerEnvironment.start()
    }

    @EventListener
    fun handleContextClosed(event: ContextClosedEvent) {
      //  GarethServerEnvironment.shutDown()
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()

        val javaTimeModule = JavaTimeModule()
        mapper.registerModule(javaTimeModule)

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        return mapper
    }


}