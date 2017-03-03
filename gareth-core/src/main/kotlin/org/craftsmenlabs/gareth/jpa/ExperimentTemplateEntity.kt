package org.craftsmenlabs.gareth.jpa

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "EXPERIMENT_TEMPLATE")
data class ExperimentTemplateEntity(@Id
                                    @GeneratedValue(strategy = GenerationType.AUTO)
                                    var id: Long? = null) {
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    lateinit var success: String
    lateinit var failure: String
    lateinit var timeline: String
}