package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.ExecutionStatus
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "EXPERIMENT")
data class ExperimentEntity(@Id
                            @GeneratedValue(strategy = GenerationType.AUTO)
                            var id: Long? = null) {
    lateinit var name: String
    lateinit var baseline: String
    lateinit var assume: String
    lateinit var success: String
    lateinit var failure: String
    lateinit var timeline: String
    lateinit var dateCreated: Timestamp
    @Enumerated(EnumType.ORDINAL)
    lateinit var result: ExecutionStatus
    var dateReady: Timestamp? = null
    var dateStarted: Timestamp? = null
    var dateWaitingForBaseline: Timestamp? = null
    var dateBaselineExecuted: Timestamp? = null
    var dateWaitingForAssume: Timestamp? = null
    var dateAssumeExecuted: Timestamp? = null
    var dateWaitingFinalizing: Timestamp? = null
    var dateFinalizingExecuted: Timestamp? = null
    var dateCompleted: Timestamp? = null
    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
    lateinit var environment: List<ExperimentEnvironmentItem>
}