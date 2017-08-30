package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.definitions.CreateDefinitionsRegistry
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.validator.model.GlueLineType
import org.junit.Test


class DefinitionsRegistryIntegrationTest {


    val registry = CreateDefinitionsRegistry(listOf(SaleOfFruit()))

    @Test
    fun testBaseline() {
        assertNotNull("get snake oil", GlueLineType.BASELINE)
        assertNotNull("sale of fruit", GlueLineType.BASELINE)
        assertNull("buying of fruit", GlueLineType.BASELINE)
    }

    @Test
    fun testAssume() {
        assertNotNull("sale of fruit has risen by 20 per cent", GlueLineType.ASSUME)
        assertNull("sale of fruit has risen by twenty per cent", GlueLineType.ASSUME)
    }

    @Test
    fun testDuration() {
        assertNotNull("next Easter", GlueLineType.TIME)
    }

    @Test
    fun testSuccess() {
        assertNotNull("send email to John", GlueLineType.SUCCESS)
    }

    @Test
    fun testFailure() {
        assertNotNull("send email to Sam", GlueLineType.FAILURE)
        assertNotNull("send text to John", GlueLineType.FAILURE)
    }

    @Test
    fun testconversionToDTO() {
        val dto = registry.toRegistryDTO()
        assertThat(dto.glueLinesPerCategory[GlueLineType.BASELINE]).hasSize(2)
        assertThat(dto.glueLinesPerCategory[GlueLineType.ASSUME]).hasSize(1)
        assertThat(dto.glueLinesPerCategory[GlueLineType.TIME]).hasSize(1)
        assertThat(dto.glueLinesPerCategory[GlueLineType.SUCCESS]).hasSize(1)
        assertThat(dto.glueLinesPerCategory[GlueLineType.FAILURE]).hasSize(2)
        val assume = dto.glueLinesPerCategory[GlueLineType.ASSUME].orEmpty().first()
        assertThat(assume.pattern).isEqualTo("^sale of fruit has risen by (\\d+?) per cent\$")
        assertThat(assume.readable).isEqualTo("sale of fruit has risen by whatever percentage")
    }

    fun assertNotNull(glueline: String, type: GlueLineType) {
        assertThat(registry.getMethod(glueline, type)).isNotNull()
    }

    fun assertNull(glueline: String, type: GlueLineType) {
        assertThat(registry.getMethod(glueline, type)).isNull()
    }

}