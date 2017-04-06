package org.craftsmenlabs.gareth

import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(basePackages = arrayOf("org.craftsmenlabs.gareth.mongo"))
@ComponentScan(basePackages = arrayOf("org.craftsmenlabs.gareth"))
class GarethValidatorConfiguration {

}