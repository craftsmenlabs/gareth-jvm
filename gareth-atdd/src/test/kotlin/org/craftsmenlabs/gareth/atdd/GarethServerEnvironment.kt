package org.craftsmenlabs.gareth.atdd

import org.slf4j.LoggerFactory
import java.io.File


object GarethServerEnvironment {

    private val log = LoggerFactory.getLogger("garethServerEnvironment")

    private val instances = mutableListOf<SpringApplicationWrapper>()
    val GARETH_PORT = 8100;
    val GARETH_EXECUTION_PORT = 8101;

    fun addInstance(instance: SpringApplicationWrapper) {
        if (instance.getStatus() == SpringApplicationWrapper.Status.IDLE)
            instances.add(instance)
    }

    fun start() {
        EmbeddedMongoManager.start()
        instances.forEach { it.start() }
    }

    fun createGarethInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = GARETH_PORT)
        conf.add("logging.level.org.craftsmenlabs", "DEBUG")
        conf.add("execution.client.url", "http://localhost:$GARETH_EXECUTION_PORT")
        conf.add("execution.client.user", "user")
        conf.add("execution.client.password", "secret")
        conf.add("execution.client.password", "secret")
        conf.add("spring.data.mongodb.port", EmbeddedMongoManager.MONGO_PORT.toString())
        conf.add("spring.data.mongodb.database", EmbeddedMongoManager.MONGO_ADDRESS)

        val jarFilePath = getJarfile(getProjectRootDir() + "gareth-core/target")
        return SpringApplicationWrapper("http://localhost:$GARETH_PORT/manage", jarFilePath, conf)
    }

    fun createExecutionInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = GARETH_EXECUTION_PORT)
        conf.add("spring.profiles.active", "test")
        val jarFilePath = getJarfile(getProjectRootDir() + "gareth-acme/target")
        return SpringApplicationWrapper("http://localhost:$GARETH_EXECUTION_PORT/manage", jarFilePath, conf)
    }

    fun shutDown() {
        log.info("Shutting down gareth environments")
        instances.forEach { it.shutdown() }
        EmbeddedMongoManager.shutDown()
    }


    fun getProjectRootDir(): String {
        val packageAsPath = GarethServerEnvironment::class.java.getPackage().getName().replace("\\.".toRegex(), "/")
        val path = GarethServerEnvironment::class.java.getResource(".").getFile()
        return path.replace("gareth-atdd/target/test-classes/$packageAsPath", "").replace("\\/\\/".toRegex(), "\\/")
    }

    fun getJarfile(path: String): String {
        val dir = File(path)
        if (!dir.isDirectory)
            throw IllegalArgumentException("$dir is not a directory")
        val files = dir.listFiles({ file: File -> file.name.contains("gareth") && file.name.endsWith("jar") })
        if (files.size != 1)
            throw IllegalStateException("Expected only one jar file in dir $path, but found ${files.size}")
        return files[0].canonicalPath
    }


}