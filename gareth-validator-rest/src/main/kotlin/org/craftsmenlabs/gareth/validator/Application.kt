package org.craftsmenlabs.gareth.validator

//import org.craftsmenlabs.gareth.GarethValidatorConfiguration
import org.craftsmenlabs.gareth.GarethValidatorConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(GarethValidatorConfiguration::class)
open class Application {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}


