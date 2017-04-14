package org.craftsmenlabs.gareth.validator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(GarethValidatorConfiguration::class)
open class GarethValidatorApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(GarethValidatorApplication::class.java, *args)
        }
    }
}


