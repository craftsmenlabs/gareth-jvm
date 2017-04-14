package org.craftsmenlabs.gareth.execution

import org.craftsmenlabs.gareth.validator.model.*
import org.springframework.util.StringUtils

class RunContext(private var data: MutableMap<String, EnvironmentItem> = mutableMapOf()) : ExecutionRunContext {

    override fun getItems(): List<EnvironmentItem> = data.values.toList()

    override fun storeString(key: String, value: String) {
        data[key] = EnvironmentItem(key, value, ItemType.STRING)
    }

    override fun getString(key: String) = getKey(key).value

    override fun storeLong(key: String, value: Long) {
        data[key] = EnvironmentItem(key, value.toString(), ItemType.LONG)
    }

    override fun getLong(key: String) = getKey(key).value.toLong()

    override fun storeList(key: String, value: List<String>) {
        data[key] = EnvironmentItem(key, StringUtils.arrayToCommaDelimitedString(value.toTypedArray()), ItemType.LIST)
    }

    override fun getList(key: String): List<String> =
            StringUtils.delimitedListToStringArray(getKey(key).value, ",").toList()

    override fun storeDouble(key: String, value: Double) {
        data[key] = EnvironmentItem(key, value.toString(), ItemType.DOUBLE)
    }

    override fun getDouble(key: String) = getKey(key).value.toDouble()

    override fun storeBoolean(key: String, value: Boolean) {
        data[key] = EnvironmentItem(key, value.toString(), ItemType.BOOLEAN)
    }


    override fun getBoolean(key: String) = getKey(key).value.toBoolean()

    fun getKey(key: String) = data[key] ?: throw IllegalArgumentException("No key $key")

    override fun toExecutionResult(status: ExecutionStatus): ExecutionResult {
        val environment = ExperimentRunEnvironment(data.values.toList())
        return ExecutionResult(environment, status)
    }

    companion object {
        fun create(request: ExecutionRequest): ExecutionRunContext {
            val data = mutableMapOf<String, EnvironmentItem>()
            request.environment.items.forEach { data.put(it.key, it) }
            return RunContext(data)
        }
    }

}