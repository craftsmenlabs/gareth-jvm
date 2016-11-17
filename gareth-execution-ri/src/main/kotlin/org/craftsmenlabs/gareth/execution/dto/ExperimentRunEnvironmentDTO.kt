package org.craftsmenlabs.gareth.execution.dto

class ExperimentRunEnvironmentDTO(val items: List<Item> = listOf()) {

    fun getValueByKey(key: String): Any? = items.filter { it.key == key }.map { it.value }.firstOrNull()

    companion object {

        fun createItem(key: String, value: Any): Item {
            if (value is Long || value is Int) {
                return Item(key, value.toString(), ItemType.LONG)
            }
            if (value is Float || value is Double) {
                return Item(key, value.toString(), ItemType.DOUBLE)
            }
            if (value is Boolean) {
                return Item(key, if (value) "true" else "false", ItemType.BOOLEAN)
            } else {
                return Item(key, value.toString(), ItemType.STRING)
            }
        }

        fun parseItem(item: Item): Any {
            val value = when (item.type) {
                ItemType.LONG -> item.value.toLong()
                ItemType.BOOLEAN -> item.value == "true"
                ItemType.DOUBLE -> item.value.toDouble()
                ItemType.STRING -> item.value
            }
            return value
        }

        fun createEmpty(): ExperimentRunEnvironmentDTO = createFromMap(mapOf())

        fun createFromMap(data: Map<String, Any>): ExperimentRunEnvironmentDTO {
            val entries = data.map { entry -> createItem(entry.key, entry.value) }
            return ExperimentRunEnvironmentDTO(entries)
        }
    }
}

data class Item(val key: String, val value: String, val type: ItemType) {

}

enum class ItemType {
    STRING, LONG, DOUBLE, BOOLEAN
}
