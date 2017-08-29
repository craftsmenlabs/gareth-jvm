package org.craftsmenlabs.gareth.validator.model

import java.time.LocalDateTime
import java.util.*

enum class ExecutionStatus {

    PENDING,
    /**
     * return after successfully executing the baseline step
     */
    RUNNING,
    /**
     * Returned after the assume step was evaluated successfully
     */
    SUCCESS,
    /**
     * Returned after the assume step was evaluated with a failure
     */
    FAILURE,
    /**
     * Returned when a non-recoverable error was encountered
     */
    ERROR;

    fun isCompleted() = this == SUCCESS || this == FAILURE || this == ERROR
}

enum class ItemType {
    STRING, LONG, DOUBLE, BOOLEAN, LIST
}

enum class GlueLineType {
    BASELINE, ASSUME, TIME, SUCCESS, FAILURE;

    companion object {
        @JvmStatic
        fun safeValueOf(key: String?): Optional<GlueLineType> {
            try {
                return if (key == null) Optional.empty() else Optional.of(GlueLineType.valueOf(key));
            } catch (e: Exception) {
                return Optional.empty();
            }
        }
    }
}

data class Duration(val unit: String, val amount: Long)

data class DefinitionInfo(val glueline: String, val method: String, val className: String, val description: String? = null, val humanReadable: String? = null)

data class ExecutionRequest(val experimentId: String, val runContext: RunContext, val glueLines: ValidatedGluelines)

data class BaselineExecutionResult(val experimentId: String, val runContext: RunContext, val success: Boolean, val assumptionDue: LocalDateTime?){
}

data class AssumeExecutionResult(val experimentId: String, val runContext: RunContext, val status: ExecutionStatus)

data class EnvironmentItem(val value: String, val itemType: ItemType)


