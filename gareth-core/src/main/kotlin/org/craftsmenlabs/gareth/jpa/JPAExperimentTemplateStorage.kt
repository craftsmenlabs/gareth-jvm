package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.ExperimentTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JPAExperimentTemplateStorage @Autowired constructor(val experimentTemplateDao: ExperimentTemplateDao, val converter: EntityConverter) {

    fun getAll(): List<ExperimentTemplate> {
        return experimentTemplateDao.findAll().map { converter.toDTO(it) }
    }

    fun save(template: ExperimentTemplate): ExperimentTemplate {
        val saved = experimentTemplateDao.save(converter.toEntity(template))
        return converter.toDTO(saved)
    }


}