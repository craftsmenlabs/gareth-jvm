package org.craftsmenlabs.gareth.atdd

import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import java.util.*


class StreamMonitor(
        private val process: Process,
        private val scanner: Scanner,
        private val _logAsWarnings: Boolean
) : Runnable {
    val logger = LoggerFactory.getLogger("StreamMonitor")
    fun start() {
        Thread(this).start()
    }

    override fun run() {
        while (process.isAlive) {
            readLine()
        }
    }

    private fun readLine() {
        if (scanner.hasNext()) {
            val line = scanner.nextLine()

            if (!StringUtils.isEmpty(line)) {
                if (_logAsWarnings) {
                    logger.warn(line)
                } else {
                    logger.info(line)
                }
            }
        }
    }
}