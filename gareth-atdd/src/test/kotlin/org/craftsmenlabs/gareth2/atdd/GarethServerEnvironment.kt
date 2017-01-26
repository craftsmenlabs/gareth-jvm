package org.craftsmenlabs.gareth2.atdd

import org.craftsmenlabs.gareth.execution.Application
import org.slf4j.LoggerFactory


class GarethServerEnvironment {

    val log = LoggerFactory.getLogger("garethServerEnvironment")
    val executionServer: GarethExecutionServer = GarethExecutionServer()

    class GarethExecutionServer : SpringApplicationWrapper() {
        val args = arrayOf("--server.port=8091", "--logging.level.org.springframework=WARN", "--logging.level.springfox=WARN", "--logging.level.org.hibernate=WARN")
        fun start() {
            setContext(Application.run(args))
        }
    }

    fun start() {
        log.info("Starting gareth environment")
        executionServer.start()
    }

    fun shutDown() {
        log.info("Shutting down gareth environment")
        SpringApplicationWrapper.closeAll(executionServer)
    }


}