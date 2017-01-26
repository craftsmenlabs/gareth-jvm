package org.craftsmenlabs.gareth.execution.dto

import org.craftsmenlabs.gareth.api.execution.EnvironmentItem
import org.craftsmenlabs.gareth.api.execution.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.api.execution.ItemType

object ExperimentRunEnvironmentBuilder {

    private fun createItem(key: String, value: Any): EnvironmentItem {
        if (value is Long || value is Int) {
            return EnvironmentItem(key, value.toString(), ItemType.LONG)
        }
        if (value is Float || value is Double) {
            return EnvironmentItem(key, value.toString(), ItemType.DOUBLE)
        }
        if (value is Boolean) {
            return EnvironmentItem(key, if (value) "true" else "false", ItemType.BOOLEAN)
        } else {
            return EnvironmentItem(key, value.toString(), ItemType.STRING)
        }
    }

    fun createEmpty(): ExperimentRunEnvironment = createFromMap(mapOf())

    fun createFromMap(data: Map<String, Any>): ExperimentRunEnvironment {
        val entries = data.map { entry -> createItem(entry.key, entry.value) }
        return ExperimentRunEnvironment(entries)
    }
}

