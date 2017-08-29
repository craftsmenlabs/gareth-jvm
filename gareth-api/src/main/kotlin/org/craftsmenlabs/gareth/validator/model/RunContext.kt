package org.craftsmenlabs.gareth.validator.model

import org.springframework.util.StringUtils

data class RunContext(private var data: MutableMap<String, EnvironmentItem> = mutableMapOf()) {

    fun getItems(): List<EnvironmentItem> = data.values.toList()

    fun storeString(key: String, value: String) {
        data[key] = EnvironmentItem(value, ItemType.STRING)
    }

    fun getString(key: String) = getKey(key).value

    fun storeLong(key: String, value: Long) {
        data[key] = EnvironmentItem(value.toString(), ItemType.LONG)
    }

    fun getLong(key: String) = getKey(key).value.toLong()

    fun storeList(key: String, value: List<String>) {
        data[key] = EnvironmentItem(StringUtils.arrayToCommaDelimitedString(value.toTypedArray()), ItemType.LIST)
    }

    fun getList(key: String): List<String> =
            StringUtils.delimitedListToStringArray(getKey(key).value, ",").toList()

    fun storeDouble(key: String, value: Double) {
        data[key] = EnvironmentItem(value.toString(), ItemType.DOUBLE)
    }

    fun getDouble(key: String) = getKey(key).value.toDouble()

    fun storeBoolean(key: String, value: Boolean) {
        data[key] = EnvironmentItem(value.toString(), ItemType.BOOLEAN)
    }


    fun getBoolean(key: String) = getKey(key).value.toBoolean()

    fun getKey(key: String) = data[key] ?: throw IllegalArgumentException("No key $key")


}