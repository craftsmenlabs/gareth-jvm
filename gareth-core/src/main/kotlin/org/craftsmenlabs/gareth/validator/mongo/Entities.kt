package org.craftsmenlabs.gareth.validator.mongo

import org.craftsmenlabs.gareth.validator.model.ExecutionStatus
import org.craftsmenlabs.gareth.validator.model.ItemType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "experiment_run")
data class ExperimentEntity(@Id var id: String? = null) {
    lateinit var dateCreated: LocalDateTime
    lateinit var result: ExecutionStatus
    lateinit var dateDue: LocalDateTime
    var dateBaselineExecuted: LocalDateTime? = null
    var dateCompleted: LocalDateTime? = null
    lateinit var environment: Set<ExperimentEnvironmentItem>
    @Indexed
    lateinit var templateId: String
    @Indexed
    lateinit var projectId: String
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    var success: String? = null
    var failure: String? = null
    lateinit var timeline: String
}

@Document(collection = "experiment_template")
data class ExperimentTemplateEntity(@Id var id: String? = null) {
    @Indexed
    lateinit var projectId: String
    @Indexed
    lateinit var name: String
    var baseline: String? = null
    var assume: String? = null
    var success: String? = null
    var failure: String? = null
    var timeline: String? = null
    lateinit var dateCreated: LocalDateTime
    var ready: LocalDateTime? = null
}

@Document(collection = "project")
data class ProjectEntity(@Id var id: String? = null) {
    lateinit var name: String;
    lateinit var companyId: String;
    var active: Boolean = false
}

class ExperimentEnvironmentItem {
    lateinit var key: String
    lateinit var value: String
    lateinit var itemType: ItemType
}