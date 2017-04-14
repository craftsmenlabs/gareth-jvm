package org.craftsmenlabs.gareth.validator.model

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
    ERROR
}

enum class ItemType {
    STRING, LONG, DOUBLE, BOOLEAN, LIST
}

enum class GlueLineType {
    BASELINE, ASSUME, TIME, SUCCESS, FAILURE;

    companion object {
        @JvmStatic fun safeValueOf(key: String?): Optional<GlueLineType> {
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

data class ExecutionRequest(val environment: ExperimentRunEnvironment, val glueLines: ValidatedGluelines)

data class ExecutionResult(val environment: ExperimentRunEnvironment, val status: ExecutionStatus)

data class EnvironmentItem(val key: String, val value: String, val itemType: ItemType)

data class ExperimentRunEnvironment(val items: List<EnvironmentItem> = listOf<EnvironmentItem>())


