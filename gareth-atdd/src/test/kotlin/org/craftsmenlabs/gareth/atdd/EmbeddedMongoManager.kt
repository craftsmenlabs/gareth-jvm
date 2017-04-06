package org.craftsmenlabs.gareth.atdd

import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.slf4j.LoggerFactory


object EmbeddedMongoManager {
    private val log = LoggerFactory.getLogger(EmbeddedMongoManager::class.java)

    val MONGO_ADDRESS = "localhost"
    val MONGO_PORT = System.getProperty("mongoport", "27018").toInt()
    lateinit var mongodExecutable: MongodExecutable

    private fun createConfig(): IMongodConfig {
        return MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(MONGO_ADDRESS, MONGO_PORT, Network.localhostIsIPv6()))
                .build()
    }

    fun start() {
        val starter = MongodStarter.getDefaultInstance()
        mongodExecutable = starter.prepare(createConfig());
        mongodExecutable.start()
        log.info("Successfully started local mongo")
    }

    fun shutDown() {
        if (EmbeddedMongoManager.mongodExecutable != null) {
            EmbeddedMongoManager.mongodExecutable.stop();
            log.info("Successfully shut down mongo.")
        } else
            log.warn("Mongo is not started. Will not shut down")
    }

    fun deleteAll() {
        val mongo = MongoClient(MONGO_ADDRESS, MONGO_PORT)
        val db = mongo.getDB("test")
        db.collectionNames.forEach { db.getCollection(it).drop() }
        log.info("Successfully dropped collections.")
    }
}