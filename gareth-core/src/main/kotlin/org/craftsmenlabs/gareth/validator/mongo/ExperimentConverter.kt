package org.craftsmenlabs.gareth.validator.mongo

import org.craftsmenlabs.gareth.validator.model.EnvironmentItem
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.craftsmenlabs.gareth.validator.model.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.validator.model.ValidatedGluelines
import org.springframework.stereotype.Service

@Service
class ExperimentConverter {

    fun copyEditableValues(entity: ExperimentEntity, experiment: ExperimentDTO): ExperimentEntity {
        entity.dateDue = experiment.due
        entity.dateBaselineExecuted = experiment.baselineExecuted
        entity.dateCompleted = experiment.completed
        entity.result = experiment.status

        val environment: List<ExperimentEnvironmentItem> = experiment.environment.items.map {
            val item = ExperimentEnvironmentItem()
            item.key = it.key
            item.value = it.value
            item.itemType = it.itemType
            item
        }
        entity.environment = environment.toSet()
        return entity
    }

    fun toDTO(entity: ExperimentEntity): ExperimentDTO {
        val gluelines = ValidatedGluelines(
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline)
        val environmentItems = entity.environment.map { EnvironmentItem(it.key, it.value, it.itemType) }
        val dto = ExperimentDTO(
                id = entity.id!!,
                projectId = entity.projectId,
                name = entity.name,
                created = entity.dateCreated,
                glueLines = gluelines,
                due = entity.dateDue,
                baselineExecuted = entity.dateBaselineExecuted,
                completed = entity.dateCompleted,
                status = entity.result,
                environment = ExperimentRunEnvironment(environmentItems))
        return dto;
    }
}