package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.execution.rest.v1.DefinitionsEndpoint
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GarethExecutionApplication::class, SaleOfFruit::class), webEnvironment = SpringBootTest.WebEnvironment.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
class DefinitionInfoIntegrationTest {

    @Autowired
    lateinit var endpoint: DefinitionsEndpoint

    @Test
    fun testBaseline() {
        val info = endpoint.getBaselineByGlueline("sale of fruit")
        assertThat(info.glueline).isEqualTo("^sale of (.*?)$")
        assertThat(info.method).isEqualTo("getSaleOfItem")
        assertThat(info.description).isEqualTo("Sale of many things")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")
    }

    @Test
    fun testDuration() {
        val duration = endpoint.getDurationByGlueline("next Easter")
        assertThat(duration.unit).isEqualTo("MINUTES")
        assertThat(duration.amount).isEqualTo(14400)
    }

    @Test
    fun testAssumptionInfo() {
        val info = endpoint.getAssumeByGlueline("sale of fruit has risen by 5 per cent")
        assertThat(info.glueline).isEqualTo("^sale of fruit has risen by (\\d+?) per cent$")
        assertThat(info.method).isEqualTo("hasRisenByPercent")
        assertThat(info.description).isEqualTo("Sale of fruit has risen by some percentage")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")
    }

    @Test
    fun testSuccessInfo() {
        val info = endpoint.getSuccessByGlueline("send email to bob")
        assertThat(info.glueline).isEqualTo("^send email to (.*?)$")
        assertThat(info.method).isEqualTo("sendText")
        assertThat(info.description).isEqualTo("Send email that the experiment succeeded.")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")
    }

    @Test
    fun testFailureInfo() {
        val info = endpoint.getFailureByGlueline("send email to John")
        assertThat(info.glueline).isEqualTo("^send email to (.*?)$")
        assertThat(info.method).isEqualTo("sendFailureEmail")
        assertThat(info.description).isEqualTo("Send email that the experiment failed.")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit")

    }

}

