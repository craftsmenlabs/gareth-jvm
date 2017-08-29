package org.craftsmenlabs.gareth.validator.mongo

import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.ValidatedGluelines
import org.springframework.stereotype.Service

@Service
class ExperimentConverter {

    fun copyEditableValues(entity: ExperimentEntity, experiment: ExperimentDTO): ExperimentEntity {
        entity.baselineDue = experiment.baselineDue
        entity.dateBaselineExecuted = experiment.baselineExecuted
        entity.dateCompleted = experiment.completed
        entity.assumeDue = experiment.assumeDue
        entity.result = experiment.status
        entity.archived = experiment.archived
        entity.runContext = experiment.runContext
        return entity
    }


    fun toDTO(entity: ExperimentEntity): ExperimentDTO {
        val gluelines = ValidatedGluelines(
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline)
        val dto = ExperimentDTO(
                id = entity.id!!,
                projectId = entity.projectId,
                name = entity.name,
                created = entity.dateCreated,
                glueLines = gluelines,
                baselineDue = entity.baselineDue,
                assumeDue =  entity.assumeDue,
                baselineExecuted = entity.dateBaselineExecuted,
                completed = entity.dateCompleted,
                status = entity.result,
                runContext = entity.runContext,
                archived = entity.archived)
        return dto;
    }
}