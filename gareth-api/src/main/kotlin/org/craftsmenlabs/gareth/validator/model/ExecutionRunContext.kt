package org.craftsmenlabs.gareth.validator.model


interface ExecutionRunContext {
    fun storeString(key: String, value: String)

    fun storeLong(key: String, value: Long)

    fun storeList(key: String, value: List<String>)

    fun storeDouble(key: String, value: Double)

    fun storeBoolean(key: String, value: Boolean)

    fun getString(key: String): String

    fun getLong(key: String): Long

    fun getList(key: String): List<String>

    fun getDouble(key: String): Double

    fun getBoolean(key: String): Boolean

    fun toExecutionResult(running: ExecutionStatus): ExecutionResult

    fun getItems(): List<EnvironmentItem>
}