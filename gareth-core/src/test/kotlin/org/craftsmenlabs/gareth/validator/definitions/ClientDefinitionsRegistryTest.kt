package org.craftsmenlabs.gareth.validator.definitions

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.ClientDefinitionsSampleFactory
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClientDefinitionsRegistryTest {

    val registry = ClientDefinitionsSampleFactory.create()

    @Test
    fun testcreate() {
        assertThat(registry.getGluelinesPerCategory(GlueLineType.BASELINE)).hasSize(1)
        assertThat(registry.getGluelinesPerCategory(GlueLineType.ASSUME)).hasSize(1)
        assertThat(registry.getGluelinesPerCategory(GlueLineType.TIME)).hasSize(1)
        assertThat(registry.getGluelinesPerCategory(GlueLineType.SUCCESS)).hasSize(1)
        assertThat(registry.getGluelinesPerCategory(GlueLineType.FAILURE)).hasSize(1)
    }
}