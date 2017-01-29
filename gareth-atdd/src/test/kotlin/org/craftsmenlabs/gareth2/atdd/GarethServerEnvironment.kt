package org.craftsmenlabs.gareth2.atdd

import org.slf4j.LoggerFactory


object GarethServerEnvironment {

    val log = LoggerFactory.getLogger("garethServerEnvironment")
    val garethInstance: SpringApplicationWrapper = createGarethInstance()
    val executionInstance: SpringApplicationWrapper = createExecutionInstance()

    fun refresh() {
        garethInstance.start()
        executionInstance.start()
    }

    private fun createGarethInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = 8090)
        return SpringApplicationWrapper("http://localhost:8090/manage", "/Users/jasper/dev/gareth-jvm/gareth-core/target/gareth-core-0.8.7-SNAPSHOT.jar", conf)
    }

    private fun createExecutionInstance(): SpringApplicationWrapper {
        val conf = ConfBuilder(port = 8091)
        return SpringApplicationWrapper("http://localhost:8091/manage", "/Users/jasper/dev/gareth-jvm/gareth-execution-ri/target/gareth-execution-ri-0.8.7-SNAPSHOT.jar", conf)
    }

    fun shutDown() {
        log.info("Shutting down gareth environment")
        garethInstance.shutdown();
        executionInstance.shutdown()
    }

    class ConfBuilder(var port: Int) {

        fun build(): List<String> {
            val items = mutableListOf<String>()
            fun add(key: String, value: String) {
                val sb = StringBuilder()
                sb.append("-D").append(key).append("=").append(value).append(" ")
                items.add(sb.toString())
            }
            add("server.port", port.toString())
            add("endpoints.shutdown.sensitive", "false")
            add("endpoints.shutdown.enabled", "true")
            add("management.context-path", "/manage")
            add("management.security.enabled", "false")
            add("logging.level.org.springframework", "WARN")
            //add("logging.level.org.hibernate", "WARN")
            return items;
        }
    }

}