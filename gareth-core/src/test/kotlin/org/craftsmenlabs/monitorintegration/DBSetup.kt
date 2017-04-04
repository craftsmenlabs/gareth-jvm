package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.mongo.MongoExperimentDao
import org.craftsmenlabs.gareth.mongo.MongoExperimentEntity
import org.craftsmenlabs.gareth.mongo.MongoExperimentTemplateDao
import org.craftsmenlabs.gareth.mongo.MongoExperimentTemplateEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DBSetup @Autowired constructor(private val experimentDao: MongoExperimentDao,
                                     private val templateDao: MongoExperimentTemplateDao) {

    fun createExperiment(): MongoExperimentEntity {
        val template = MongoExperimentTemplateEntity()
        template.name = "name"
        template.baseline = "b"
        template.assume = "a"
        template.success = "s"
        template.failure = "f"
        template.timeline = "t"
        val saved = templateDao.save(template)
        val entity = MongoExperimentEntity()
        entity.baseline = saved.baseline
        entity.assume = saved.assume
        entity.success = saved.success
        entity.failure = saved.failure
        entity.timeline = saved.timeline
        entity.name = saved.name

        val now = LocalDateTime.now()
        entity.dateCreated = now
        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING
        return experimentDao.save(entity)
    }
}