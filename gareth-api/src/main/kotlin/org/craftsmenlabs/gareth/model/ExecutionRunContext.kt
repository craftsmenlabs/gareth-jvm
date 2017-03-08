package org.craftsmenlabs.gareth.model


interface  ExecutionRunContext {
    fun storeString(key: String, value: String)

    fun storeLong(key: String, value: Long)

    fun storeDouble(key: String, value: Double)

    fun storeBoolean(key: String, value: Boolean)

    fun getString(key: String): String

    fun getLong(key: String): Long

    fun getDouble(key: String): Double

    fun getBoolean(key: String): Boolean

    fun toExecutionResult(running: ExecutionStatus): ExecutionResult
}