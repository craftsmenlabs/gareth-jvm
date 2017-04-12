package org.craftsmenlabs.gareth.services

import org.craftsmenlabs.gareth.mongo.ExperimentDao
import org.craftsmenlabs.gareth.mongo.ExperimentEntity
import org.craftsmenlabs.gareth.mongo.ExperimentTemplateDao
import org.craftsmenlabs.gareth.mongo.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.OverviewDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OverviewService @Autowired constructor(private val experimentDao: ExperimentDao,
                                             private val templateDao: ExperimentTemplateDao,
                                             private val timeService: TimeService) {

    fun getAllForProject(projectId: String): List<OverviewDTO> {
        val templatesByProject = templateDao.findByProjectId(projectId)
        val experimentsByTemplate: Map<String, List<ExperimentEntity>> = experimentDao.findByProjectId(projectId).groupBy { it.templateId }
        return if (templatesByProject.isEmpty()) listOf() else templatesByProject.map { createForTemplate(it, experimentsByTemplate[it.id]) }
    }

    private fun createForTemplate(template: ExperimentTemplateEntity,
                                  experiments: List<ExperimentEntity>?): OverviewDTO {
        if (experiments == null || experiments.isEmpty())
            return OverviewDTO(name = template.name, templateId = template.id!!, editable = true, ready = template.ready != null)
        val finished = experiments.filter { it.dateCompleted != null }
        val success = finished.filter { it.result == ExecutionStatus.SUCCESS }
        val failed = finished.filter { it.result == ExecutionStatus.FAILURE }
        val pending = experiments.filter { it.dateDue != null && it.result == ExecutionStatus.PENDING }
        val running = experiments.filter { it.result == ExecutionStatus.RUNNING }

        val lastRun = finished.map { it.dateCompleted!! }.max()
        //dateStarted is guaranteed to be non null
        val nextRun = experiments.filter { it.dateDue != null && it.dateDue!!.isAfter(timeService.now()) }.map { it.dateDue!! }.min()
        val ready = template.ready != null
        return OverviewDTO(
                name = template.name,
                templateId = template.id!!,
                editable = !ready,
                ready = ready,
                lastRun = lastRun,
                nextRun = nextRun,
                pending = pending.size,
                running = running.size,
                success = success.size,
                failed = failed.size)
    }

}