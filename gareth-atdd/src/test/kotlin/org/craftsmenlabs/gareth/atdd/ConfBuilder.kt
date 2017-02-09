package org.craftsmenlabs.gareth.atdd

class ConfBuilder(var port: Int) {
    val items = mutableListOf<String>()

    fun add(key: String, value: String) {
        val sb = StringBuilder()
        sb.append("--").append(key).append("=").append(value)
        items.add(sb.toString())
    }

    fun build(): List<String> {
        add("server.port", port.toString())
        add("endpoints.shutdown.sensitive", "false")
        add("endpoints.shutdown.enabled", "true")
        add("management.context-path", "/manage")
        add("management.security.enabled", "false")
        add("logging.level.org.springframework", "WARN")
        return items;
    }
}