package org.craftsmenlabs.gareth.execution.integration

import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.execution.GarethExecutionApplication
import org.craftsmenlabs.gareth.execution.definitions.SaleOfFruit
import org.craftsmenlabs.gareth.model.GlueLineSearchResultDTO
import org.craftsmenlabs.gareth.rest.BasicAuthenticationRestClient
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(GarethExecutionApplication::class, SaleOfFruit::class), webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
@TestPropertySource(properties = arrayOf("server.port=8101"))
class GlueLineSearchIntegrationTest {

    val path = "http://localhost:8101/gareth/validator/v1/search/"
    val restClient = BasicAuthenticationRestClient("user", "secret")

    @Test
    fun testBaseline() {
        assertThat(get("${path}baseline", "sale of fruit").suggestions).containsExactly("sale of *")
    }

    @Test
    fun testAssume() {
        assertThat(get("${path}assume", "sale of fruit has risen").suggestions).containsExactly("sale of fruit has risen by * per cent")
        assertThat(get("${path}assume", "sale of fruit has risen").exact).isNull()

        assertThat(get("${path}assume", "sale of fruit has risen by 20 per cent").exact).isEqualTo("sale of fruit has risen by * per cent")
    }

    @Test
    fun testDuration() {
        assertThat(get("${path}time", "1 week").exact).isEqualTo("* weeks?")
    }

    @Test
    fun testSuccess() {
        assertThat(get("${path}success", "send email to John").exact).isEqualTo("send email to *")
    }

    @Test
    fun testFailure() {
        assertThat(get("${path}failure", "send email to John").exact).isEqualTo("send email to *")
        assertThat(get("${path}failure", "send").suggestions).contains("send email to *", "send text to *")
    }

    fun get(path: String, glueLine: String): GlueLineSearchResultDTO {
        val response = restClient.getAsEntity(GlueLineSearchResultDTO::class.java, "$path/$glueLine")
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        return response.body
    }

}

