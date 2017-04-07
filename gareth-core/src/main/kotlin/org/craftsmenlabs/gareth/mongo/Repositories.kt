package org.craftsmenlabs.gareth.mongo

import org.springframework.data.mongodb.repository.MongoRepository

interface ExperimentDao : MongoRepository<ExperimentEntity, String> {
    fun findByTemplateId(templateId: String): List<ExperimentEntity>
    fun findByProjectId(projectId: String): List<ExperimentEntity>
}

interface ExperimentTemplateDao : MongoRepository<ExperimentTemplateEntity, String> {
    fun findByName(name: String): ExperimentTemplateEntity?
    fun findByProjectId(name: String): List<ExperimentTemplateEntity>

}

