package org.craftsmenlabs.gareth.mongo

import org.craftsmenlabs.gareth.model.EnvironmentItem
import org.craftsmenlabs.gareth.model.ExperimentDTO
import org.craftsmenlabs.gareth.model.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentConverter @Autowired constructor(val timeService: TimeService) {

    fun copyEditableValues(entity: MongoExperimentEntity, experiment: ExperimentDTO): MongoExperimentEntity {

        entity.dateDue = experiment.due ?: timeService.now()
        entity.dateBaselineExecuted = experiment.baselineExecuted
        entity.dateCompleted = experiment.completed
        entity.result = experiment.result

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

    fun toDTO(entity: MongoExperimentEntity): ExperimentDTO {
        val gluelines = Gluelines(
                assume = entity.assume,
                baseline = entity.baseline,
                success = entity.success,
                failure = entity.failure,
                time = entity.timeline)
        val environmentItems = entity.environment.map { EnvironmentItem(it.key, it.value, it.itemType) }
        val dto = ExperimentDTO(
                id = entity.id!!,
                name = entity.name,
                created = entity.dateCreated,
                glueLines = gluelines,
                due = entity.dateDue,
                baselineExecuted = entity.dateBaselineExecuted,
                completed = entity.dateCompleted,
                result = entity.result,
                environment = ExperimentRunEnvironment(environmentItems))
        return dto;
    }
}