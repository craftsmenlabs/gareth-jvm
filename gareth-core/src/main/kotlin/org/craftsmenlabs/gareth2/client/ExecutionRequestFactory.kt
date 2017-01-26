package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.api.execution.EnvironmentItem
import org.craftsmenlabs.gareth.api.execution.ExecutionRequest
import org.craftsmenlabs.gareth.api.execution.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.api.execution.ItemType
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails

class ExecutionRequestFactory {

    fun createForGluelineType(type: GlueLineType, experiment: Experiment): ExecutionRequest {
        val runEnvironment = createEnvironment(experiment.environment)
        val glueLine = getGlueLine(type, experiment.details)
        return ExecutionRequest(runEnvironment, glueLine)
    }

    private fun getGlueLine(type: GlueLineType, details: ExperimentDetails): String {
        return when (type) {
            GlueLineType.ASSUME -> details.assume
            GlueLineType.BASELINE -> details.baseline
            GlueLineType.SUCCESS -> details.success
            GlueLineType.FAILURE -> details.failure
            GlueLineType.TIME -> details.time
        }
    }

    private fun createEnvironmentItem(key: String, value: Any): EnvironmentItem {
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

    private fun createEnvironment(data: Map<String, Any>): ExperimentRunEnvironment {
        val entries = data.map { entry -> createEnvironmentItem(entry.key, entry.value) }
        return ExperimentRunEnvironment(entries)
    }

}


