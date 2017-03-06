package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.ExecutionStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "EXPERIMENT")
data class ExperimentEntity(@Id
                            @GeneratedValue(strategy = GenerationType.AUTO)
                            var id: Long? = null) {
    @Convert(converter = DateTimeConverter::class)
    lateinit var dateCreated: LocalDateTime
    @Enumerated(EnumType.ORDINAL)
    lateinit var result: ExecutionStatus
    @Convert(converter = DateTimeConverter::class)
    var dateReady: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateStarted: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateWaitingForBaseline: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateBaselineExecuted: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateWaitingForAssume: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateAssumeExecuted: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateWaitingFinalizing: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateFinalizingExecuted: LocalDateTime? = null
    @Convert(converter = DateTimeConverter::class)
    var dateCompleted: LocalDateTime? = null
    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, mappedBy = "experiment", orphanRemoval = true)
    lateinit var environment: Set<ExperimentEnvironmentItem>
    @ManyToOne(fetch = FetchType.EAGER)
    lateinit var template: ExperimentTemplateEntity
}

@Entity(name = "EXPERIMENT_TEMPLATE")
data class ExperimentTemplateEntity(@Id
                                    @GeneratedValue(strategy = GenerationType.AUTO)
                                    var id: Long? = null){
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    lateinit var success: String
    lateinit var failure: String
    lateinit var timeline: String
    @Convert(converter = DateTimeConverter::class)
    lateinit var dateCreated: LocalDateTime
    @Convert(converter = DateTimeConverter::class)
    var ready: LocalDateTime? = null
}