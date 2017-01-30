package org.craftsmenlabs.gareth2.atdd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
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
        //TODO disabled for now until the mechanism to start/stop the environment works properly
        // you have to make sure both gareth servers are running for the atdd test to succeed
        //GarethServerEnvironment.refresh()
    }

    @EventListener
    fun handleContextClosed(event: ContextClosedEvent) {
        //TODO disabled for now: you have to make sure both gareth servers are running for the atdd test to succeed
        // GarethServerEnvironment.shutDown()
    }

}