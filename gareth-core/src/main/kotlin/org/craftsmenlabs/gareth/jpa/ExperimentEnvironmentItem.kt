package org.craftsmenlabs.gareth.jpa

import org.craftsmenlabs.gareth.model.ItemType
import javax.persistence.*

@Entity(name = "EXPERIMENT_ENVIRONMENT_ITEM")
class ExperimentEnvironmentItem(@Id
                                @GeneratedValue(strategy = GenerationType.AUTO)
                                var id: Long? = null) {
    lateinit var key: String
    lateinit var value: String
    @Enumerated(EnumType.STRING)
    lateinit var itemType: ItemType
}


