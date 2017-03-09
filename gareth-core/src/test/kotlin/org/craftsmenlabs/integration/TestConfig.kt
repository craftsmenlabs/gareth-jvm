package org.craftsmenlabs.gareth.integration

import org.craftsmenlabs.gareth.rest.RestClientConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.integration"))
@Import(RestClientConfig::class)
class TestConfig {


}