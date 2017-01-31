package org.craftsmenlabs.gareth.execution

import org.craftsmenlabs.gareth.model.*

class RunContext(private var data: MutableMap<String, EnvironmentItem>) : ExecutionRunContext {

    override fun storeString(key: String, value: String) {
        data[key] = EnvironmentItem(key, value, ItemType.STRING)
    }

    override fun getString(key: String) = getKey(key).value

    override fun storeLong(key: String, value: Long) {
        data[key] = EnvironmentItem(key, value.toString(), ItemType.LONG)
    }

    override fun getLong(key: String) = getKey(key).value.toLong()


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