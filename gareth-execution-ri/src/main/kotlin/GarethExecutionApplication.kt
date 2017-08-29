package org.craftsmenlabs.gareth.execution

import org.craftsmenlabs.gareth.validator.beans.GarethCommonsConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.execution"))
@Import(GarethCommonsConfig::class)
open class GarethExecutionApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(GarethExecutionApplication::class.java, *args)
        }

        fun run(args: Array<String>): ConfigurableApplicationContext = SpringApplication.run(GarethExecutionApplication::class.java, *args)
    }
}


