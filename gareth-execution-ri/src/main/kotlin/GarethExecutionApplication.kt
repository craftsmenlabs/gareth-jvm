package org.craftsmenlabs.gareth.execution

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.execution"))
open class GarethExecutionApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(GarethExecutionApplication::class.java, *args)
        }

        fun run(args: Array<String>): ConfigurableApplicationContext = SpringApplication.run(GarethExecutionApplication::class.java, *args)
    }
}


