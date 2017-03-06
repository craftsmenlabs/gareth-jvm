package org.craftsmenlabs.monitorintegration

import org.craftsmenlabs.gareth.jpa.ExperimentDao
import org.craftsmenlabs.gareth.jpa.ExperimentEntity
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateDao
import org.craftsmenlabs.gareth.jpa.ExperimentTemplateEntity
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DBSetup @Autowired constructor(private val experimentDao: ExperimentDao,
                                     private val templateDao: ExperimentTemplateDao) {

    fun createExperiment(): ExperimentEntity {
        val template = ExperimentTemplateEntity()
        template.name = "name"
        template.baseline = "b"
        template.assume = "a"
        template.success = "s"
        template.failure = "f"
        template.timeline = "t"
        val saved = templateDao.save(template)
        val entity = ExperimentEntity()
        entity.template = saved
        val now = LocalDateTime.now()
        entity.dateCreated = now
        entity.environment = setOf()
        entity.result = ExecutionStatus.PENDING
        return experimentDao.save(entity)
    }
}