package org.craftsmenlabs.gareth

import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentTemplateCreateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateDTO
import org.craftsmenlabs.gareth.model.ExperimentTemplateUpdateDTO
import java.time.LocalDateTime


interface ExperimentStorage {

    fun getAllTemplates(): List<ExperimentTemplateDTO>
    fun getTemplateById(id: String): ExperimentTemplateDTO
    fun createTemplate(dto: ExperimentTemplateCreateDTO): ExperimentTemplateDTO
    fun updateTemplate(dto: ExperimentTemplateUpdateDTO): ExperimentTemplateDTO

    fun loadAllExperiments(): List<Experiment>
    fun updateExperiment(experiment: Experiment): Experiment
    fun createExperiment(templateId: String, startDate: LocalDateTime?): Experiment
    fun setListener(listener: ((Experiment) -> Unit)?)
    fun getById(id: String): Experiment
    fun getFiltered(createdAfter: LocalDateTime? = null, onlyFinished: Boolean? = null): List<Experiment>
}