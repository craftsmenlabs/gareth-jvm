package org.craftsmenlabs.gareth2.client

import org.craftsmenlabs.gareth.api.execution.ExecutionRequest
import org.craftsmenlabs.gareth.api.model.GlueLineType
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails

class ExecutionRequestFactory {

    fun createForGluelineType(type: GlueLineType, experiment: Experiment): ExecutionRequest {
        val glueLine = getGlueLine(type, experiment.details)
        return ExecutionRequest(experiment.environment, glueLine)
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


}


