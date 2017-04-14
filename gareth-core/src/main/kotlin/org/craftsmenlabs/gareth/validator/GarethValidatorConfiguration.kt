package org.craftsmenlabs.gareth.validator

import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(basePackages = arrayOf("org.craftsmenlabs.gareth.validator.mongo"))
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth.validator"))
class GarethValidatorConfiguration {

}