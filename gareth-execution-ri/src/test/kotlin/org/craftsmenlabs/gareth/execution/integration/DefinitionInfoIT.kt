package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.Application
import org.craftsmenlabs.gareth.execution.rest.v1.DefinitionsEndpoint
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class), webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DefinitionInfoIT {

    @Autowired
    lateinit var endpoint: DefinitionsEndpoint

    @Test
    fun testBaseline() {
        val info = endpoint.getBaselineByGlueline("sale of fruit")
        assertThat(info.glueline).isEqualTo("^sale of (.*?)$")
        assertThat(info.method).isEqualTo("getSaleOfItem")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.spi.GetSaleAmounts")
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
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.spi.SaleOfFruit")
    }

    @Test
    fun testSuccessInfo() {
        val info = endpoint.getSuccessByGlueline("send email to bob")
        assertThat(info.glueline).isEqualTo("^send email to (.*?)$")
        assertThat(info.method).isEqualTo("sendEmail")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.spi.ResultSteps")
    }

    @Test
    fun testFailureInfo() {
        val info = endpoint.getFailureByGlueline("send email to John")
        assertThat(info.glueline).isEqualTo("^send email to (.*?)$")
        assertThat(info.method).isEqualTo("sendFailureEmail")
        assertThat(info.className).isEqualTo("org.craftsmenlabs.gareth.execution.spi.ResultSteps")
    }

}

