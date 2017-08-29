package org.craftsmenlabs.gareth.validator.beans

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.validator.beans"))
open class GarethCommonsConfig {
}