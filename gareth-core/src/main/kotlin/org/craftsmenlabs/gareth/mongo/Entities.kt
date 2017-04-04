package org.craftsmenlabs.gareth.mongo

import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.ItemType
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class MongoExperimentEntity(@Id var id: String? = null) {
    lateinit var dateCreated: LocalDateTime
    lateinit var result: ExecutionStatus
    lateinit var dateDue: LocalDateTime
    var dateBaselineExecuted: LocalDateTime? = null
    var dateCompleted: LocalDateTime? = null
    lateinit var environment: Set<ExperimentEnvironmentItem>
    lateinit var templateId: String
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    lateinit var success: String
    lateinit var failure: String
    lateinit var timeline: String
}

data class MongoExperimentTemplateEntity(@Id var id: String? = null) {
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    lateinit var success: String
    lateinit var failure: String
    lateinit var timeline: String
    lateinit var dateCreated: LocalDateTime
    var ready: LocalDateTime? = null
}

class ExperimentEnvironmentItem {
    lateinit var key: String
    lateinit var value: String
    lateinit var itemType: ItemType
}