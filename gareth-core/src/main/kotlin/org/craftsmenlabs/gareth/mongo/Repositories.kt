package org.craftsmenlabs.gareth.mongo

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoExperimentDao : MongoRepository<MongoExperimentEntity, String> {
    fun findByTemplateId(templateId: String): List<MongoExperimentEntity>
    fun findByProjectId(projectId: String): List<MongoExperimentEntity>
}

interface MongoExperimentTemplateDao : MongoRepository<MongoExperimentTemplateEntity, String> {
    fun findByName(name: String): MongoExperimentTemplateEntity?
}

