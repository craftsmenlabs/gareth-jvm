package org.craftsmenlabs.gareth.validator.integration

import org.craftsmenlabs.gareth.GarethValidatorApplication
import org.springframework.context.annotation.Import


//@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.validator.integration"))
@Import(GarethValidatorApplication::class)
class TestConfig {


}