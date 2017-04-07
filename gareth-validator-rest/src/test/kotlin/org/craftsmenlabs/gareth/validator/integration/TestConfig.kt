package org.craftsmenlabs.gareth.validator.integration

import org.craftsmenlabs.gareth.rest.RestClientConfig
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import


@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.validator.integration"))
@Import(RestClientConfig::class, EmbeddedMongoAutoConfiguration::class)
class TestConfig {


}