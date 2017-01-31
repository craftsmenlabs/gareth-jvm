package org.craftsmenlabs.gareth2.atdd

class ConfBuilder(var port: Int,
                  val executionClientUrl: String? = null,
                  val executionClientUser: String? = null,
                  val executionClientPassword: String? = null) {
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
        if (executionClientUrl != null)
            add("execution.client.url", executionClientUrl)
        if (executionClientUser != null)
            add("execution.client.user", executionClientUser)
        if (executionClientPassword != null)
            add("execution.client.password", executionClientPassword)
        return items;
    }
}