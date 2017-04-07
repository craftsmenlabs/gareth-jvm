package org.craftsmenlabs.gareth.execution.dto

import org.assertj.core.api.Assertions
import org.craftsmenlabs.gareth.model.ItemType
import org.junit.Test

class ExperimentRunEnvironmentDTOTest {

    @Test
    fun createEnvironmentFromMap() {
        val env = ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("someInt", 1), Pair("someLong", 3L), Pair("someBoolean", true)))
        Assertions.assertThat(env.items).extracting("key", String::class.java).containsExactly("someInt", "someLong", "someBoolean")
        Assertions.assertThat(env.items).extracting("itemType", ItemType::class.java).containsExactly(ItemType.LONG, ItemType.LONG, ItemType.BOOLEAN)
    }

}