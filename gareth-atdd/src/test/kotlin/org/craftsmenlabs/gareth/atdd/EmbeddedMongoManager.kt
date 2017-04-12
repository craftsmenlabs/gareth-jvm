package org.craftsmenlabs.gareth.atdd

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
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

    val MONGO_HOSTNAME = "localhost"
    val MONGO_DATABASE = "atdd"
    val PROJECT_ID = "SALE_OF_FRUIT"
    val MONGO_PORT = System.getProperty("mongoport", "27018").toInt()
    private val netconfig = Net(MONGO_HOSTNAME, MONGO_PORT, Network.localhostIsIPv6())
    lateinit var mongodExecutable: MongodExecutable

    private fun createConfig(): IMongodConfig = MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(netconfig)
            .build()

    fun start() {
        runImport()
        MongodStarter.getDefaultInstance().prepare(createConfig()).start()
        log.info("Successfully started local mongo")
    }

    private fun runImport() {
        val mongo = MongoClient(MONGO_HOSTNAME, MONGO_PORT)
        val db = mongo.getDB(MONGO_DATABASE)
        val project = BasicDBObject()
        project.put("_id", "{\"\$oid\": \"$PROJECT_ID\"}")
        project.put("_class", "org.craftsmenlabs.dashboard.models.db.Project")
        project.put("name", "sale of fruit")
        project.put("companyId", "58e650ef443ca94506af5808")
        project.put("active", true)
        val objects = listOf<DBObject>(project)
        db.getCollection("project").insert(objects)
    }

    fun shutDown() {
        if (EmbeddedMongoManager.mongodExecutable != null) {
            EmbeddedMongoManager.mongodExecutable.stop();
            log.info("Successfully shut down mongo.")
        } else
            log.warn("Mongo is not started. Will not shut down")
    }

    fun deleteAll() {
        val mongo = MongoClient(MONGO_HOSTNAME, MONGO_PORT)
        val db = mongo.getDB(MONGO_DATABASE)
        db.getCollection("experimentTemplateEntity").drop()
        db.getCollection("experimentEntity").drop()
        log.info("Successfully dropped collections.")
    }
}