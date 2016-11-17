package org.craftsmenlabs.gareth.execution

import org.craftsmenlabs.gareth.execution.dto.*

class RunContext(private var data: MutableMap<String, Item>) {

    fun storeString(key: String, value: String) {
        data[key] = Item(key, value, ItemType.STRING)
    }

    fun getString(key: String) = getKey(key).value

    fun storeLong(key: String, value: Long) {
        data[key] = Item(key, value.toString(), ItemType.LONG)
    }

    fun getLong(key: String) = getKey(key).value.toLong()


    fun storeDouble(key: String, value: Double) {
        data[key] = Item(key, value.toString(), ItemType.DOUBLE)
    }

    fun getDouble(key: String) = getKey(key).value.toDouble()

    fun storeBoolean(key: String, value: Boolean) {
        data[key] = Item(key, value.toString(), ItemType.BOOLEAN)
    }


    fun getBoolean(key: String) = getKey(key).value.toBoolean()

    fun getKey(key: String) = data[key] ?: throw IllegalArgumentException("No key $key")

    fun toExecutionResult(status: ExecutionStatus): ExecutionResultDTO {
        val env = ExperimentRunEnvironmentDTO(data.values.toList())
        return ExecutionResultDTO(status, env)
    }

    companion object {
        fun create(request: ExecutionRequestDTO): RunContext {
            val data = mutableMapOf<String, Item>()
            request.environment.items.forEach { data.put(it.key, it) }
            return RunContext(data)
        }
    }


}