package org.craftsmenlabs.gareth.execution

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.validator.model.EnvironmentItem
import org.craftsmenlabs.gareth.validator.model.ItemType
import org.craftsmenlabs.gareth.validator.model.RunContext
import org.junit.Test

class RunContextTest {

    @Test
    fun testStringToValue() {
        val context = RunContext(mutableMapOf<String, EnvironmentItem>(
                Pair("LONG", EnvironmentItem("123", ItemType.LONG)),
                Pair("LIST", EnvironmentItem("The,quick,brown,fox", ItemType.LIST)),
                Pair("STRING", EnvironmentItem("Hello!", ItemType.STRING)),
                Pair("BOOLEAN", EnvironmentItem("true", ItemType.BOOLEAN)),
                Pair("DOUBLE", EnvironmentItem("0.42", ItemType.DOUBLE))))
        assertThat(context.getBoolean("BOOLEAN")).isTrue()
        assertThat(context.getString("STRING")).isEqualTo("Hello!")
        assertThat(context.getLong("LONG")).isEqualTo(123)
        assertThat(context.getDouble("DOUBLE")).isEqualTo(0.42)
        assertThat(context.getList("LIST")).containsExactly("The", "quick", "brown", "fox")
    }

    @Test
    fun valueToString() {
        val context = RunContext(mutableMapOf<String, EnvironmentItem>())
        context.storeBoolean("BOOLEAN", true)
        context.storeString("STRING", "Hello!")
        context.storeLong("LONG", 123)
        context.storeDouble("DOUBLE", 0.42)
        context.storeList("LIST", listOf("The", "quick", "brown", "fox"))

        assertThat(context.getBoolean("BOOLEAN")).isTrue()
        assertThat(context.getString("STRING")).isEqualTo("Hello!")
        assertThat(context.getLong("LONG")).isEqualTo(123)
        assertThat(context.getDouble("DOUBLE")).isEqualTo(0.42)
        assertThat(context.getList("LIST")).containsExactly("The", "quick", "brown", "fox")
        assertThat(context.getItems().map { it.value }).containsExactlyInAnyOrder("true", "Hello!", "123", "0.42", "The,quick,brown,fox")
    }
}