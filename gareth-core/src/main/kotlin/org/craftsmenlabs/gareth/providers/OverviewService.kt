package org.craftsmenlabs.gareth.providers

import org.craftsmenlabs.gareth.jpa.ExperimentDao
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateDao
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.OverviewDTO
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OverviewService @Autowired constructor(private val templateDao: ExperimentTemplateDao,
                                             private val experimentDao: ExperimentDao,
                                             private val timeService: TimeService) {

    fun getAll(): List<OverviewDTO> {
        val templates = templateDao.findAll().toList()
        return if (templates.isEmpty()) listOf() else templates.map { createForTemplate(it) }
    }

    private fun createForTemplate(template: ExperimentTemplateEntity): OverviewDTO {
        val experiments = experimentDao.findByTemplate(template)
        if (experiments.isEmpty())
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