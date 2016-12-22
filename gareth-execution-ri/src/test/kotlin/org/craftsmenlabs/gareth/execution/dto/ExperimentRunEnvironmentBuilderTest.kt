package org.craftsmenlabs.gareth.execution.dto

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.api.execution.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.api.execution.ItemType
import org.junit.Test

class ExperimentRunEnvironmentBuilderTest {

    @Test
    fun testFloat() {
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", 0.2f))), "0.2", ItemType.DOUBLE)
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", 0.2))), "0.2", ItemType.DOUBLE)
    }

    @Test
    fun testLong() {
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", 42L))), "42", ItemType.LONG)
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", 42))), "42", ItemType.LONG)
    }

    @Test
    fun testBoolean() {
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", false))), "false", ItemType.BOOLEAN)
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", true))), "true", ItemType.BOOLEAN)
    }

    @Test
    fun testString() {
        doAssert(ExperimentRunEnvironmentBuilder.createFromMap(mapOf(Pair("k", "Hello world"))), "Hello world", ItemType.STRING)
    }

    @Test
    fun testemptyMap() {
        assertThat(ExperimentRunEnvironmentBuilder.createEmpty().items).isEmpty()
    }

    private fun doAssert(environment: ExperimentRunEnvironment, key: String, type: ItemType) {
        val item = environment.items.find { it.key == "k" } ?: throw IllegalStateException("not found")
        assertThat(item.value).isEqualTo(key)
        assertThat(item.itemType).isEqualTo(type)
    }


}