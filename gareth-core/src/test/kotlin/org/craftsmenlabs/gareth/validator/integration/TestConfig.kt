package org.craftsmenlabs.gareth.validator.integration

import org.craftsmenlabs.gareth.validator.GarethValidatorApplication
import org.springframework.context.annotation.Import

@Import(GarethValidatorApplication::class, MockServiceDiscovery::class)
class TestConfig {


}