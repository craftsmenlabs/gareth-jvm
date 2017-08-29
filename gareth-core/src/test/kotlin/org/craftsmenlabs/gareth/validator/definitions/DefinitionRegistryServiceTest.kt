package org.craftsmenlabs.gareth.validator.definitions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.craftsmenlabs.gareth.validator.model.DefinitionRegistryDTO
import org.junit.Test

class DefinitionRegistryServiceTest {

    val registry = DefinitionRegistryService()

    @Test
    fun testregisterDefinitionTwicePerformsUpdate() {
        registry.registerDefinitionsForClient("ACME", DefinitionRegistryDTO())
        val registryForClient = registry.getRegistryForClient("ACME")
        registry.registerDefinitionsForClient("ACME", DefinitionRegistryDTO())
        assertThat(registry.getRegistryForClient("ACME")).isNotSameAs(registryForClient)
    }

    @Test
    fun testUnknownProject() {
        registry.registerDefinitionsForClient("ACME", DefinitionRegistryDTO())
        assertThatThrownBy { registry.getRegistryForClient("MEAC") }.hasMessage("not a valid project: MEAC")

    }
}