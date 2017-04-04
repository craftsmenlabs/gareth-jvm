package org.craftsmenlabs.gareth.atdd

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.slf4j.LoggerFactory


object EmbeddedMongo {
    private val log = LoggerFactory.getLogger(EmbeddedMongo::class.java)

    val bindIp = "localhost"
    val port = 27017
    lateinit var mongodExecutable: MongodExecutable

    private fun createConfig(): IMongodConfig {
        return MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(bindIp, port, Network.localhostIsIPv6()))
                .build()
    }

    fun start() {
        val starter = MongodStarter.getDefaultInstance()
        mongodExecutable = starter.prepare(createConfig());
        mongodExecutable.start()
        log.info("Successfully started mongo")
    }

    fun shutDown() {
        if (EmbeddedMongo.mongodExecutable != null) {
            EmbeddedMongo.mongodExecutable.stop();
            log.info("Successfully shut down mongo.")
        } else
            log.info("Mongo is not started. Will not shut down")
    }
}