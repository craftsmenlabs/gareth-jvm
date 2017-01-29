package org.craftsmenlabs.gareth2.atdd

import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Callable


class SpringApplicationWrapper(val managementUrl: String, val executable: String, val configuration: GarethServerEnvironment.ConfBuilder) {

    enum class Status {
        IDLE, STARTING, STARTED, STOPPING, STOPPED
    }

    private var status: Status = Status.IDLE

    private val log = LoggerFactory.getLogger("SpringApplicationWrapper")

    val restClient = BasicAuthenticationRestClient("user", "secret")
    val startupMonitor = StartupMonitor()
    val shutdownMonitor = ShutDownMonitor()


    fun isStarted(): Boolean {
        return status == Status.IDLE
    }

    fun isStopped(): Boolean {
        return status == Status.STOPPED
    }

    fun start() {
        if (status != Status.IDLE) {
            log.info("Cannot start service. status is $status")
            return;
        }
        status = Status.STARTING
        val arguments: MutableList<String> = mutableListOf("java", "-jar")
        //arguments.addAll(configuration.build())
        arguments.add(executable)
        val pBuilder = ProcessBuilder(arguments)
        val process = pBuilder.start()
        //java -jar -Dserver.port=8090 -Dendpoints.shutdown.sensitive=false -Dendpoints.shutdown.enabled=true
        // -Dmanagement.context-path=/manage -Dmanagement.security.enabled=false
        // /Users/jasper/dev/gareth-jvm/gareth-core/target/gareth-core-0.8.7-SNAPSHOT.jar

        fun readStream(input: InputStream) {
            val scanner = Scanner(InputStreamReader(input, Charset.forName("UTF-8")))
            StreamMonitor(process, scanner, false).start()
        }

        readStream(process.inputStream)
        readStream(process.errorStream)
        if (!process.isAlive) {
            throw IllegalStateException("Not started")
        }
        try {
            return waitUntil(30, startupMonitor)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to start server")
        }
        log.info("Server is up")
    }


    fun shutdown() {
        if (status != Status.STARTED) {
            log.error("Can only shut down running server, but status is $status")
        }
        status = Status.STOPPING
        try {
            return waitUntil(60, shutdownMonitor)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to close server")
        }
    }

    private fun waitUntil(atMost: Long, condition: Callable<Boolean>) {
        Thread.sleep(3000)
        var counter: Int = 0
        while (counter < atMost) {
            if (condition.call()) {
                return;
            }
            Thread.sleep(1000)
            counter = counter + 1
        }
        throw IllegalStateException("Could not execute within given time")
    }

    inner class StartupMonitor : Callable<Boolean> {
        override fun call(): Boolean {
            try {
                val response = restClient.getAsEntity(JSONObject::class.java, "$managementUrl/mappings")
                val isStarted = response != null && response.statusCode.is2xxSuccessful
                log.info("Waiting for instance to start: $isStarted")
                if (isStarted) {
                    status = Status.STARTED
                }
                return isStarted
            } catch(e: Exception) {
                return false;
            }
        }
    }

    inner class ShutDownMonitor : Callable<Boolean> {
        override fun call(): Boolean {
            try {
                val response = restClient.postAsEntity("", JSONObject::class.java, "$managementUrl/shutdown")
                log.info("Waiting for instance to shutdown: $response")
                val isStopped = response != null && response.statusCode.is2xxSuccessful
                if (isStopped) {
                    status = Status.STOPPED
                }
                return isStopped
            } catch(e: Exception) {
                return false;
            }
        }
    }

}