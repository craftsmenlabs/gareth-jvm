package org.craftsmenlabs.gareth2.atdd

import org.slf4j.LoggerFactory


object GarethServerEnvironment {

    private val log = LoggerFactory.getLogger("garethServerEnvironment")

    private val instances = mutableListOf<SpringApplicationWrapper>()
    val garethPort = 8100;
    val executionPort = 8101;

    fun addInstance(instance: SpringApplicationWrapper) {
        if (instance.getStatus() == SpringApplicationWrapper.Status.IDLE)
            instances.add(instance)
    }

    fun start() {
        instances.forEach { it.start() }
    }

    fun createGarethInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = garethPort, executionClientUrl = "http://localhost:$executionPort", executionClientUser = "user", executionClientPassword = "secret")
        return SpringApplicationWrapper("http://localhost:$garethPort/manage", "/Users/jasper/dev/gareth-jvm/gareth-core/target/gareth-core-0.8.7-SNAPSHOT.jar", conf)
    }

    fun createExecutionInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = executionPort)
        conf.add("definitions.package", "org.craftsmenlabs.gareth.execution.spi")
        return SpringApplicationWrapper("http://localhost:$executionPort/manage", "/Users/jasper/dev/gareth-jvm/gareth-execution-ri/target/gareth-execution-ri-0.8.7-SNAPSHOT.jar", conf)
    }

    fun shutDown() {
        log.info("Shutting down gareth environments")
        instances.forEach { it.shutdown() }
    }

}