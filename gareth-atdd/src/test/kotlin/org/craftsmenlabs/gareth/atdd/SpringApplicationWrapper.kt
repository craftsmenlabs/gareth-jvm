package org.craftsmenlabs.gareth.atdd

import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Callable


class SpringApplicationWrapper(val managementUrl: String, val executable: String, val configuration: ConfBuilder) {

    enum class Status {
        IDLE, STARTING, STARTED, STOPPING, STOPPED;

        fun isIdle() = this == IDLE
        fun isStarting() = this == STARTING
        fun isStarted() = this == STARTED
        fun isStopping() = this == STOPPING
        fun isStopped() = this == STOPPED
    }

    private var status: Status = Status.IDLE
    private val waitForStartupSeconds = 15L;
    private val waitForShutdownSeconds = 5L;

    private val log = LoggerFactory.getLogger("SpringApplicationWrapper")

    private val restClient = BasicAuthenticationRestClient("user", "secret")
    private val startupMonitor = StartupMonitor()
    private val shutdownMonitor = ShutDownMonitor()

    fun getStatus() = status

    fun start() {
        if (status != Status.IDLE) {
            log.info("Cannot start service. status is $status")
            return;
        }
        status = Status.STARTING
        startServer()
        try {
            waitUntil(waitForStartupSeconds, startupMonitor)
            if (status == Status.STOPPING) {
                throw IllegalStateException("Could not start service within time.")
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to start server")
        }
        log.info("Server is up")
    }

    private fun startServer() {
        val arguments: MutableList<String> = mutableListOf("java", "-jar", executable)
        arguments.addAll(configuration.build())
        val pBuilder = ProcessBuilder(arguments)
        val process = pBuilder.start()
        fun readStream(input: InputStream) {
            val scanner = Scanner(InputStreamReader(input, Charset.forName("UTF-8")))
            StreamMonitor(process, scanner, false).start()
        }

        readStream(process.inputStream)
        readStream(process.errorStream)
        if (!process.isAlive) {
            throw IllegalStateException("Not started")
        }
    }


    fun shutdown() {
        if (status != Status.STARTED) {
            log.error("Can only shut down running server, but status is $status")
        }
        status = Status.STOPPING
        try {
            waitUntil(waitForShutdownSeconds, shutdownMonitor)
            if (status == Status.STOPPING) {
                throw IllegalStateException("Could not stop service within time.")
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to close server")
        }
    }

    private fun waitUntil(atMost: Long, condition: Callable<Boolean>) {
        Thread.sleep(3000)
        var counter: Int = 0
        while (counter < atMost) {
            if (condition.call()) {
                break;
            }
            Thread.sleep(1000)
            counter = counter + 1
        }
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